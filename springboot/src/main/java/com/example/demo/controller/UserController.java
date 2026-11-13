package com.example.demo.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.TokenUtils;
import com.example.demo.entity.Log;
import com.example.demo.entity.RBAC.*;
import com.example.demo.mapper.*;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;
import com.example.demo.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.common.TokenUtils.getUser;

/**
 * @author Geed
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    RoleMapper roleMapper;
    @Resource
    PermissionMapper permissionMapper;
    @Resource
    IpMapper ipMapper;

    @Resource
    UserService userService;
    @Resource
    LogService logService;

    private String getIp(HttpServletRequest request){
        return request.getHeader("ip");
    }

    /**
     * 后台新增用户
     * @param user 弹窗数据
     * @return 结果
     */
    @PostMapping
    public Result<?> save(@RequestBody User user){
        if (user.getPassword() == null){
            user.setPassword("111111");
        }
        userService.save(user);
        return Result.success();
    }

    /**
     * 用户登录
     * @param user e
     * @param request req
     * @return rst
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user, HttpServletRequest request){
        User result = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword()));
        if (result == null){
            return Result.error("-1", "用户名或密码错误");
        }else if (result.getBan() == 1){
            return Result.error("-1","您的账号已被封禁，请联系管理员咨询");
        }
        else{
            Integer userId = result.getId();
            HashSet<Permission> permissions = new HashSet<>();
            List<UserRole> userRoles = roleMapper.getUserRoleByUserId(userId);
            // 设置角色id
            result.setRoles(userRoles.stream().map(UserRole::getRoleId).distinct().collect(Collectors.toList()));
            for (UserRole userRole : userRoles){
                List<RolePermission> rolePermissions = permissionMapper.getRolePermissionByRoleId(userRole.getRoleId());
                for (RolePermission rolePermission : rolePermissions) {
                    Integer permissionId = rolePermission.getPermissionId();
                    Permission permission = permissionMapper.selectById(permissionId);
                    permissions.add(permission);
                }
            }
            // 对资源根据id进行排序
            LinkedHashSet<Permission> sortedSet = permissions.stream().sorted(Comparator.comparing(Permission::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
            result.setPermissions(sortedSet);
            //生成token
            String token = TokenUtils.generateToken(result.getId().toString(), result.getPassword());
            result.setToken(token);
            //获取Ip，如果是新ip就保存到数据库中
            String ip = getIp(request);
            Long ipCount = ipMapper.selectCount(Wrappers.<Ip>lambdaQuery().eq(Ip::getUserId, result.getId()).eq(Ip::getIp, ip));
//            boolean match = ipList.stream().anyMatch(item -> item.getIp().equals(ip));   效率可能稍慢
//            if(!match){
            if (ipCount==0){
                Ip neoIp = new Ip();
                neoIp.setIp(ip);
                neoIp.setUserId(result.getId());
                ipMapper.insert(neoIp);
            }
            Log log = new Log("用户："+user.getUsername()+"登录成功~", new Date());
            logService.save(log);
            return Result.success(result);
        }
    }

    /**
     * 用户注册
     * @param user e
     * @param request req
     * @return rst
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user, HttpServletRequest request){
        User result = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()));
        String ip = getIp(request);
        List<Integer> rst = ipMapper.searchBan(ip);
        if (result != null){
            return Result.error("-1", "用户名已存在~");
        }
        else if(rst.stream().anyMatch(p->p == 1)){
            Log log = new Log("有人尝试在封禁IP："+ip+"上注册用户。", new Date());
            logService.save(log);

            return Result.error("-1", "您的ip之前的用户有封禁记录，无法注册~");
        }
        else{
            //用户名不重复且没有封禁记录即可注册
            userService.save(user);
            result = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()));
            roleMapper.insertUserRole(result.getId(), 2);
            //写入系统日志
            Log log = new Log("新用户："+user.getUsername()+"注册成功~", new Date());
            logService.save(log);
            return Result.success();
        }
    }

    /**
     * 后台中更新单个用户数据
     * @param user
     * @return
     */
    @PutMapping
    public Result<?> update(@RequestBody User user){
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return rst
     */
    @GetMapping("/username/{username}")
    public Result<?> getUpdatedUser(@PathVariable String username){
        return Result.success(userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username)));
    }

    // 改变权限接口
    @PutMapping("/changeRole")
    public Result<?> changeRole(@RequestBody User user) {
        // 先根据角色id删除所有的角色跟权限的绑定关系
        roleMapper.deleteRoleByUserId(user.getId());
        // 再新增 新的绑定关系
        for (Integer roleId : user.getRoles()) {
            roleMapper.insertUserRole(user.getId(), roleId);
        }

        // 获取当前登录用户的角色id列表
        User currentUser = getUser();
        // 如果当前登录用户的角色列表包含需要修改的角色id，那么就重新登录
        assert currentUser != null;
        if (user.getId().equals(currentUser.getId())) {
            return Result.success(true);
        }
//        如果不包含，则返回false，不需要重新登录。
        return Result.success(false);
    }

    //根据id删除单个数据
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        userService.removeById(id);
        return Result.success();
    }

    /**
     * 账号封禁，可选择是否banIp
     * @param user 用户
     * @return result
     */
    @PutMapping("/ban")
    public Result<?> ban(@RequestBody User user){
        boolean update = userService.updateById(user);
        if (update){
            int result = ipMapper.updateByUserid(user.getId(), user.getBan());
            if (result == 1) {
                return Result.success();
            } else {
                return Result.error("-1", "error");
            }
        }
        return Result.error("-1", "error");
    }

    //根据id批量删除
    @DeleteMapping("/batch")
    public Result<?> deleteBatch(@RequestParam String[] array){
        if (array.length != 0){
            Integer[] arr = new Integer[array.length];
            for (int i=0;i<array.length;i++){
                arr[i] = Integer.parseInt(URLDecoder.decode(array[i]));
            }

            userService.removeByIds(CollUtil.toList(arr));
            return Result.success("批量删除成功");
        }else{
            return Result.error("-1", "你倒是选了再删啊！");
        }
    }

    // 利用分页插件，模糊查询数据库中的数据
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search){
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        // 如果search不为空加上，查到null的数据
        if (StrUtil.isNotBlank(search)){
            wrapper.like(User::getUsername, search);
        }
        Page<User> page = userService.page(new Page<>(pageNum, pageSize), wrapper);
        for (User record : page.getRecords()) {
            List<UserRole> roles = roleMapper.getUserRoleByUserId(record.getId());
            List<Integer> roleIds = roles.stream().map(UserRole::getRoleId).distinct().collect(Collectors.toList());
            record.setRoles(roleIds);
        }
        return Result.success(page);
    }

    // 跳转根据用户ID查询单条数据
    @GetMapping("/id")
    public Result<?> find(@RequestParam Integer id){
        User user = userService.getById(id);
        if (user != null){
            return Result.success(user);
        }
        return Result.error("-1", "error");
    }

    /**
     * 导出数据库的数据到文件里发送到浏览器
     * @param response res
     * @throws Exception e
     */
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) throws Exception{
        //自定义表格
        List<Map<String, Object>> list = CollUtil.newArrayList();

        List<User> all = userService.list(null);
        for (User user : all) {
            Map<String, Object> row1 = new LinkedHashMap<>();
            row1.put("用户名", user.getUsername());
            row1.put("昵称", user.getNickname());
            row1.put("年龄", user.getAge());
            row1.put("性别", user.getSex());
            row1.put("地址", user.getAddress());
            list.add(row1);
        }

        // 将List写入Excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        //设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        //发送文件
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);
    }

    /**
     * 将浏览器上传的excel用户信息保存到数据库中
     * @param file 上传文件
     * @return result
     * @throws IOException e
     */
    @CrossOrigin
    @PostMapping("/import")
    public Result<?> upload(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<List<Object>> lists = ExcelUtil.getReader(inputStream).read(1);
        List<User> saveList = new ArrayList<>();
        for (List<Object> row : lists) {
            User user = new User();
            user.setUsername(row.get(0).toString());
            user.setNickname(row.get(1).toString());
            user.setAge(Integer.valueOf(row.get(2).toString()));
            user.setSex(row.get(3).toString());
            user.setAddress(row.get(4).toString());
            saveList.add(user);
        }
        for (User user : saveList) {
            userService.saveOrUpdate(user);
        }
        return Result.success();
    }
}