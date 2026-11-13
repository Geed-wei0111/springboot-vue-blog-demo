package com.example.demo.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Files;
import com.example.demo.entity.RBAC.User;
import com.example.demo.service.FilesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import static com.example.demo.common.TokenUtils.getUser;

/**
 * @author Geed
 */
@RestController
@RequestMapping("/files")
public class FilesController {
    @Resource
    private FilesService filesService;

    @Value("${server.port}")
    private String port;

    @Value("${file.ip}")
    private String ip;

    /**
     * 下载接口
     * @param flag UUID
     * @param response re
     */
    @GetMapping("/{flag}")
    public void getFiles(@PathVariable String flag, HttpServletResponse response){
        OutputStream os;  // 新建一个输出流对象
        String basePath = System.getProperty("user.dir") + "/springboot/src/main/resources/files/";  // 定于文件上传的根路径
        List<String> fileNames = FileUtil.listFileNames(basePath);  // 获取所有的文件名称
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");  // 找到跟参数一致的文件
        try {
            if (StrUtil.isNotEmpty(fileName)) {
//                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                response.addHeader("Access-Control-Allow-Credentials","true");
//                response.setContentType("application/octet-stream");
                response.setContentType("image/png");
//                response.setContentType("audio/mp3");
                byte[] bytes = FileUtil.readBytes(basePath + fileName);  // 通过文件的路径读取文件字节流
                os = response.getOutputStream();   // 通过输出流返回文件
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件下载失败");
        }
    }

    @CrossOrigin
    @PostMapping("/avatar")
//    @CacheEvict(value="getFileAll", allEntries=true)
    public JSON avatarUpload(MultipartFile file, HttpServletRequest request) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String rootFilepath =  System.getProperty("user.dir") + "/avatar/" + flag + "_" + originalFilename;
        FileUtil.writeBytes(file.getBytes(), rootFilepath);
        String url = "http://" + ip + ":" + port + "/files/avatar/" + flag;
        //拼json返回数据
        JSONObject json = new JSONObject();
        //获取文件扩展名
        String fileType = FileUtil.extName(originalFilename);

        //判断是否为常用图片格式
        if (fileType != null){
            if ("png".equals(fileType) || "jpg".equals(fileType)) {
                url += ".jpg";
                json.set("code", "0");
            }else{
                json.set("code","-1");
                json.set("msg","不是图片文件，请重试！");
                return json;
            }
        }

        User user = getUser();//要在文件上传控件添加headers.token，否则无法获取用户信息
        if (user != null){
    //            按确定按钮后，再保存，而不是一上传就保存
    //            userMapper.updateAvatarById(url, user.getId());
            json.set("type", fileType);
            json.set("url", url);
        }else{
            json.set("code","-1");
            json.set("msg", "请求错误，稍等或重新登录后尝试~");
        }
        return json;
    }

    /**
     * 预览头像接口
     * @param flag f
     * @param response rs
     */
    @GetMapping("/avatar/{flag}.**")
    public void getAvatar(@PathVariable String flag, HttpServletResponse response){
        OutputStream os;  // 新建一个输出流对象
        String basePath = System.getProperty("user.dir") + "/avatar/";  // 定于文件上传的根路径
        List<String> fileNames = FileUtil.listFileNames(basePath);  // 获取所有的文件名称
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");  // 找到跟参数一致的文件
        try {
            if (StrUtil.isNotEmpty(fileName)) {
//                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                response.addHeader("Access-Control-Allow-Credentials","true");
//                response.setContentType("application/octet-stream");
                response.setContentType("image/png");
//                response.setContentType("audio/mp3");
                byte[] bytes = FileUtil.readBytes(basePath + fileName);  // 通过文件的路径读取文件字节流
                os = response.getOutputStream();   // 通过输出流返回文件
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件下载失败");
        }
    }

    /**
     * 根据md5查询数据库中是否有同一文件
     * @param md5 文件md5
     * @return file
     */
    private Files findByMd5(String md5){
        LambdaQueryWrapper<Files> query = Wrappers.<Files>lambdaQuery().eq(Files::getMd5, md5);
        List<Files> list = filesService.list(query);
        return list.size() == 0 ? null : list.get(0);//如果有多个md5一样的文件，那么返回第一个即可
    }

    @PutMapping("/update")
//    @CacheEvict(value="getFileAll", allEntries=true)
    public Result<?> update(@RequestBody Files file){
        return Result.success(filesService.updateById(file));
    }

    @CrossOrigin
    @PostMapping("/upload3")
    public Result<?> upload3(MultipartFile file) throws IOException{
        //获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        //获取文件类型
        String type = FileUtil.extName(originalFilename);
        //获取文件大小
        long size = file.getSize()/1024;
        //生成唯一标识
        String flag = IdUtil.fastSimpleUUID();
        String rootFilepath =  System.getProperty("user.dir") + "/files/" + flag + "_" + originalFilename;
        String url;
        if (type != null){
            //音乐文件特殊处理
            if (type.equals("wav") || type.equals("mp3")){
                url = "http://" +ip + StrUtil.C_COLON + port + "/files/play/" + flag + StrUtil.DOT + type;
            }
            else{//其他类型照常用download请求
                url = "http://" + ip + ":" + port + "/files/download/" + flag + StrUtil.DOT + type;
            }
        }else{
            //无类型那就不加后缀type
            url = "http://" + ip + ":" + port + "/files/download/" + flag;
        }
        //写入文件
        FileUtil.writeBytes(file.getBytes(), rootFilepath);
        //获取文件的md5
        File newFile = new File(rootFilepath);
        String md5 = SecureUtil.md5(newFile);
        Files files = findByMd5(md5);
        Integer existId = null;
        if (files != null){
            files.setName(originalFilename);
            files.setId(null);
            filesService.save(files);
            FileUtil.del(newFile);
            existId = files.getId();
        }else{
            Files newFiles = new Files();
            newFiles.setName(originalFilename);
            newFiles.setType(type);
            newFiles.setSize(size);
            newFiles.setMd5(md5);
            newFiles.setUrl(url);
            filesService.save(newFiles);
            existId = newFiles.getId();
        }

//        获取插入数据库后的最后一个数据的ID（ 风险 ：猜测两人同时上传时，会导致返回的ID一样，并且目前已知插入后，对象会自带ID，故废除该代码 ）
//        List<Files> list = filesService.list(null);
//        return Result.success(list.get(list.size()-1).getId());
        return Result.success(existId);
    }

    @GetMapping("/getList")
//    @Cacheable(value = "getFileAll" ,key = "methodName +#p0 +#p1 + #p2 + #p3")//启用缓存：value是缓存名字，key是标识
    public Result<?> getList(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "") String search,
                             @RequestParam(defaultValue = "none") String type,
                             @RequestParam(defaultValue = "") String username,
                             @RequestParam(defaultValue = "false") boolean open){
        LambdaQueryWrapper<Files> wrapper = Wrappers.lambdaQuery();
//        如果search不为空加上，查到null的数据
        if (StrUtil.isNotBlank(search)){
            wrapper.like(Files::getName, search);
        }
        switch (type){
            case "music":wrapper.eq(Files::getType, "mp3").or().eq(Files::getType, "wav");break;
            case "image":wrapper.eq(Files::getType, "jpg").or().eq(Files::getType, "png");break;
            case "video":wrapper.eq(Files::getType, "mp4");break;
            case "package":wrapper.eq(Files::getType, "rar").or().eq(Files::getType, "zip");break;
            case "office"://TODO Office文件的查询
            case "none":
            default:break;
        }
        if (open) {
            wrapper.eq(Files::getEnabled, 1);
        }
        if (StrUtil.isNotBlank(username)){
            wrapper.or().eq(Files::getUser, username);
        }
        Page<Files> page = filesService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    //根据id删除单个数据
    @DeleteMapping("/{id}")
//    @CacheEvict(value="getFileAll", allEntries=true)
    public Result<?> delete(@PathVariable Long id){
        filesService.removeById(id);
        return Result.success();
    }

    //根据id批量删除
    @DeleteMapping("/batch")
//    @CacheEvict(value="getFileAll", allEntries=true)
    public Result<?> deleteBatch(@RequestParam String[] array){
        if (array.length != 0){
            Integer[] arr = new Integer[array.length];
            for (int i=0;i<array.length;i++){
                arr[i] = Integer.parseInt(URLDecoder.decode(array[i]));
            }

            for (Integer integer : arr) {
                filesService.removeById(integer);
            }
            return Result.success("批量删除成功");
        }else{
            return Result.error("-1", "你倒是选了再删啊！");
        }
    }

    /**
     * 新的下载接口
     * @param flag uuid
     * @param response re
     * @throws IOException e
     */
    @GetMapping("/download/{flag}.**")
    public void download(@PathVariable String flag, HttpServletResponse response) throws IOException {
        ServletOutputStream os = response.getOutputStream();
        String basePath = System.getProperty("user.dir") + "/files/";  // 定于文件上传的根路径
        List<String> fileNames = FileUtil.listFileNames(basePath);  // 获取所有的文件名称
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");  // 找到跟参数一致的文件
        try {
            if (StrUtil.isNotEmpty(fileName)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
//                response.addHeader("Access-Control-Allow-Credentials","true");
                response.setContentType("application/octet-stream");
//                response.setContentType("image/png");
//                response.setContentType("audio/mp3");
                byte[] bytes = FileUtil.readBytes(basePath + fileName);  // 通过文件的路径读取文件字节流
                os.write(bytes);
                os.flush();
                os.close();
            }else{
                System.out.println("not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件下载失败");
        }
    }

    /**
     * 富文本文件上传接口
     * @param file f
     * @return json
     * @throws IOException e
     */
    @CrossOrigin
    @PostMapping("/editor/upload")
    public JSON editorUpload(MultipartFile file) throws IOException {
        // 获取源文件的名称
        String originalFilename = file.getOriginalFilename();
        // 定义文件的唯一标识（前缀）
        // 获取上传的路径
        String flag = IdUtil.fastSimpleUUID();
        String rootFilePath = System.getProperty("user.dir") + "/springboot/src/main/resources/files/" + flag + "_" + originalFilename;
        File saveFile = new File(rootFilePath);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        // 把文件写入到上传的路径
        FileUtil.writeBytes(file.getBytes(), rootFilePath);
        String url = "http://" + ip + ":" + port + "/files/" + flag;

        JSONObject json = new JSONObject();
        json.set("errno", 0);

        // 返回结果 url
        JSONArray arr = new JSONArray();
        JSONObject data = new JSONObject();
        arr.add(data);
        data.set("url", url);
        json.set("data", arr);
        return json;
    }

    /**
     * playMedia
     * 播放音乐（或视频）的请求，可以断点续传带缓存
     * @param request rt
     * @param response rs
     */
    @GetMapping("/play/{flag}.**")
    public void playMedia(HttpServletRequest request, HttpServletResponse response, @PathVariable String flag) {
        BufferedInputStream bis;

        String basePath = System.getProperty("user.dir") + "/files/";  // 定于文件上传的根路径
        List<String> fileNames = FileUtil.listFileNames(basePath);  // 获取所有的文件名称
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");  // 找到跟参数一致的文件

        try {
            File file = new File(basePath + fileName);
            if (file.exists()) {
                long p = 0L;
                long toLength = 0L;
                long contentLength = 0L;
                int rangeSwitch = 0; // 0,从头开始的全文下载；1,从某字节开始的下载（bytes=27000-）；2,从某字节开始到某字节结束的下载（bytes=27000-39000）
                long fileLength;
                String rangBytes = "";
                fileLength = file.length();

                // get file content
                InputStream ins = new FileInputStream(file);
                bis = new BufferedInputStream(ins);

                // tell the client to allow accept-ranges
                response.reset();
                response.setHeader("Accept-Ranges", "bytes");

                // client requests a file block download start byte
                String range = request.getHeader("Range");
                if (range != null && range.trim().length() > 0 && !"null".equals(range)) {
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
                    rangBytes = range.replaceAll("bytes=", "");
                    if (rangBytes.endsWith("-")) { // bytes=270000-
                        rangeSwitch = 1;
                        p = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
                        contentLength = fileLength - p; // 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节）
                    } else { // bytes=270000-320000
                        rangeSwitch = 2;
                        String temp1 = rangBytes.substring(0, rangBytes.indexOf("-"));
                        String temp2 = rangBytes.substring(rangBytes.indexOf("-") + 1, rangBytes.length());
                        p = Long.parseLong(temp1);
                        toLength = Long.parseLong(temp2);
                        contentLength = toLength - p + 1; // 客户端请求的是 270000-320000 之间的字节
                    }
                } else {
                    contentLength = fileLength;
                }

                // 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。
                // Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
                response.setHeader("Content-Length", new Long(contentLength).toString());

                // 断点开始
                // 响应的格式是:
                // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                if (rangeSwitch == 1) {
                    String contentRange = new StringBuffer("bytes ").append(new Long(p).toString()).append("-")
                            .append(new Long(fileLength - 1).toString()).append("/")
                            .append(new Long(fileLength).toString()).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else if (rangeSwitch == 2) {
                    String contentRange = range.replace("=", " ") + "/" + new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else {
                    String contentRange = new StringBuffer("bytes ").append("0-").append(fileLength - 1).append("/")
                            .append(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                }

                fileName = file.getName();
                response.setContentType("audio/wav");
//                response.setContentType("application/octet-stream");
//                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
                String[] arr = fileName.split("_");
                response.addHeader("Content-Disposition", ";filename=" + URLEncoder.encode(arr[1], "UTF-8"));

                OutputStream out = response.getOutputStream();
                int n = 0;
                long readLength = 0;
                int bsize = 1024;
                byte[] bytes = new byte[bsize];
                if (rangeSwitch == 2) {
                    // 针对 bytes=27000-39000 的请求，从27000开始写数据
                    while (readLength <= contentLength - bsize) {
                        n = bis.read(bytes);
                        readLength += n;
                        out.write(bytes, 0, n);
                    }
                    if (readLength <= contentLength) {
                        n = bis.read(bytes, 0, (int) (contentLength - readLength));
                        out.write(bytes, 0, n);
                    }
                } else {
                    while ((n = bis.read(bytes)) != -1) {
                        out.write(bytes, 0, n);
                    }
                }
                out.flush();
                out.close();
                bis.close();
            }
        } catch (IOException ie) {
            // 忽略 ClientAbortException 之类的异常
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
