package com.example.demo.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import com.example.demo.service.*;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.common.TokenUtils.getUser;

/**
 * 博客控制器（包括点赞，收藏，访问）
 * @author Geed
 */
@RestController
@RequestMapping("/news")
public class NewsController {
    @Resource
    private NewsService newsService;
    @Resource
    private TagsService tagsService;
    @Resource
    private FolderService folderService;
    @Resource
    private LikeService likeService;
    @Resource
    private FavoriteService favoriteService;
    @Resource
    private VisitService visitService;

    @Resource
    private LogMapper logMapper;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private static final String ASC = "asc";
    private static final String DESC = "desc";
    private static final String LATEST_BLOGS = "latest_blogs";
    private static final String MOST_LIKES = "most_likes";
    private static final String MOST_FAVORITE = "most_favorite";

    /**
     * 新增博客
     * @param news n
     * @param arr a
     * @return rst
     */
    @PostMapping
    public Result<?> save(@RequestBody News news, @RequestParam String[] arr){
        //先存入博客
        news.setDate(new Date());
        newsService.save(news);
        Log log = new Log("用户:"+news.getAuthor()+"发布了一篇博客~名为："+news.getTitle(), new Date());
        logMapper.insert(log);
        //再添加标签到数据库中
        Integer blogId = news.getId();
        if (arr.length  != 0){
            for (int i = 0;i < arr.length;i++){
                String temp = arr[i];
                arr[i] = URLDecoder.decode(temp);
            }
        }
        ArrayList<String> list = CollUtil.toList(arr);
        for (String str : list) {
            Tags tag = new Tags(str, blogId);
            tagsService.save(tag);
        }
        //博客增加后，将缓存刷新
        try{
            stringRedisTemplate.delete(LATEST_BLOGS);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.success();
    }

    /**
     * 更新单个数据
     * @param news e
     * @param arr tags
     * @return rst
     */
    @PutMapping
    public Result<?> update(@RequestBody News news, @RequestParam String[] arr){
        if (arr.length != 0){
            for (int i = 0;i < arr.length;i++){
                String temp = arr[i];
                arr[i] = URLDecoder.decode(temp);
            }
        }
        //数据库中的旧标签（对象）
        List<Tags> oldTags = tagsService.list(Wrappers.<Tags>lambdaQuery().eq(Tags::getBlogId, news.getId()));
        //前端传来的新标签
        ArrayList<String> neos = CollUtil.toList(arr);
        //数据库中的旧标签（属性）
        List<String> olds = new ArrayList<>();
        for (Tags oldTag : oldTags) {
            olds.add(oldTag.getTag());
        }
        for (String neo : neos) {
            //判断前端传来的标签是否为新的标签（与数据库中的做对比）
            boolean contains = olds.contains(neo);
            if(!contains){
                Tags tags = new Tags(neo, news.getId());
                tagsService.save(tags);
            }
        }
        for (String old : olds) {
            //判断数据库中的标签，是否有被删除
            boolean contains = neos.contains(old);
            if (!contains){
                tagsService.remove(Wrappers.<Tags>lambdaQuery().eq(Tags::getBlogId, news.getId()).eq(Tags::getTag, old));
            }
        }

        //更新博客内容
        newsService.updateById(news);
        Log log = new Log("用户:"+ Objects.requireNonNull(getUser()).getUsername()+"修改了博客："+news.getTitle(), new Date());
        logMapper.insert(log);
        return Result.success();
    }

    /**
     * 根据id删除某条博客
     * @param id blog_id
     * @return rst
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        newsService.removeById(id);
        tagsService.remove(Wrappers.<Tags>lambdaQuery().eq(Tags::getBlogId, id));
        Log log = new Log("用户:"+ Objects.requireNonNull(getUser()).getUsername()+"删除了一篇博客", new Date());
        logMapper.insert(log);
        return Result.success();
    }

    /**
     * 利用分页插件，模糊查询数据库中的数据
     * @param pageNum 页码
     * @param pageSize 页容量
     * @param search 搜索框
     * @return 博客s
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String order,
                              @RequestParam(defaultValue = "") String tag,
                              @RequestParam(defaultValue = "") String author,
                              @RequestParam(defaultValue = "") String type,
                              @RequestParam(defaultValue = "-1") Integer year,
                              @RequestParam(defaultValue = "-1") Integer month
        ){
        LambdaQueryWrapper<News> wrapper = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(tag)) {
            //先找出含有该标签的博客IDs
            List<Object> ids = tagsService.listObjs(Wrappers.<Tags>lambdaQuery().eq(Tags::getTag, tag).select(Tags::getBlogId));
            StringJoiner idString = new StringJoiner(",");
            for (Object id : ids) {
                idString.add(id.toString());
            }
            //用sql的where in (...)筛选数据库
            wrapper.inSql(News::getId, idString.toString());
        }

        //  如果变量不为空加上，查到null的数据
        if (StrUtil.isNotBlank(search)){
            wrapper.like(News::getTitle, search);
        }
        if (ASC.equals(order)){
            wrapper.orderByAsc(News::getDate);
            wrapper.orderByAsc(News::getId);
        }
        if (DESC.equals(order)){
            wrapper.orderByDesc(News::getDate);
            wrapper.orderByDesc(News::getId);
        }
        if (StrUtil.isNotBlank(author)) {
            wrapper.eq(News::getAuthor, author);
        }
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(News::getType, type);
        }
        if (-1!=year && -1!=month){
            Date date = new Date(year-1900, month+1, 0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            wrapper.like(News::getDate, format.format(date));
        }
        Page<News> page = newsService.page(new Page<>(pageNum, pageSize), wrapper);

        //将分页数据中博客的标签插入到分页中
        for (News record : page.getRecords()) {
            Integer blogId = record.getId();
            record.setTags(tagsService.getTagsByBlodId(blogId));
        }
        return Result.success(page);
    }

    /**
     * 获取最新发布的5个博客
     * @return rst
     */
    @GetMapping("/latest")
    public Result<?> getLatest(){
        // redis运用
        //根据key获取缓存信息
        String jsonStr;
        List<News> news;
        try{
            jsonStr = stringRedisTemplate.opsForValue().get(LATEST_BLOGS);
        }catch (RedisConnectionFailureException e){
            news = newsService.selectLatestBlogs();
            return Result.success(news);
        }
        //如果字符串为空代表没有缓存，需要从数据库重新取
        if (StrUtil.isBlank(jsonStr)) {
            news = newsService.selectLatestBlogs();
            stringRedisTemplate.opsForValue().set(LATEST_BLOGS, JSONUtil.toJsonStr(news));
        }else {
            //字符串不空，则将其转换为List返回到前端
            news = JSONUtil.toBean(jsonStr, new TypeReference<List<News>>() {}, true);
        }
        return Result.success(news);
    }

    /**
     * 根据用户的后台记录，推荐其他博客
     * @param userId 用户id
     * @return rst
     */
    @GetMapping("/random")
    public Result<?> getRandom(@RequestParam(defaultValue = "")Integer userId){
        // 如果没传参，默认从header的token里取出来用户ID
        if (userId == null){
            userId = Objects.requireNonNull(getUser()).getId();
        }
        // 获取点赞过博客的IDs
        List<Object> blogIdsFromLike = likeService.listBlogidByUseridOrderById(userId);
        // 获取收藏夹里博客的IDs
        List<Object> blogIdsFromFavors = favoriteService.listBlogidByUseridOrderById(userId);
        // 获取浏览记录里博客的IDs
        List<Object> blogIdsFromVisit = visitService.listBlogidByUseridOrderById(userId);
        // 统计上述博客的标签出现的次数（频率）
        LinkedHashMap collect = getTagsCount(blogIdsFromLike);
        LinkedHashMap collect2 = getTagsCount(blogIdsFromFavors);
        LinkedHashMap collect3 = getTagsCount(blogIdsFromVisit);
        collect.putAll(collect2);
        collect.putAll(collect3);

        Page<News> page = new Page<>(1, 5);
        page.setRecords(new LinkedList<>());
        int randomSize = collect.size();
        Object[] tags = collect.keySet().toArray();
        if (randomSize<5){
            int minor = 5 - randomSize;
            page = newsService.page(new Page<>(1, minor), Wrappers.<News>lambdaQuery().last("order by RAND()"));
            for (Object tag : tags) {
                Object id = tagsService.listObjs(Wrappers.<Tags>lambdaQuery()
                        .select(Tags::getBlogId)
                        .eq(Tags::getTag, tag)
                        .last("order by RAND() limit 1"))
                        .get(0);
                News one = newsService.getOne(Wrappers.<News>lambdaQuery().eq(News::getId, id));
                page.getRecords().add(one);
            }
        }else{
            for (int i=0;i<5;i++){
                Object id = tagsService.listObjs(Wrappers.<Tags>lambdaQuery()
                        .select(Tags::getBlogId)
                        .eq(Tags::getTag, tags[i])
                        .last("order by RAND() limit 1"))
                        .get(0);
                News one = newsService.getOne(Wrappers.<News>lambdaQuery().eq(News::getId, id));
                page.getRecords().add(one);
            }
        }
        page.setSize(5);
        page.setTotal(5);
        return Result.success(page.getRecords());
    }

    /**
     * 根据博客IDs获取所有标签出现的次数
     * @param blogIds 博客IDs
     * @return Map
     */
    private LinkedHashMap getTagsCount(List<Object> blogIds){
        Map<String, Integer> tagsCount = new HashMap<>();
        for (Object id : blogIds) {
            List<Object> tags = tagsService.listObjs(Wrappers.<Tags>lambdaQuery().select(Tags::getTag).eq(Tags::getBlogId, id));
            for (Object tag : tags) {
                if (tagsCount.containsKey(tag)) {
                    int value = tagsCount.get(tag);
                    tagsCount.put((String) tag, value+1);
                }else{
                    tagsCount.put((String) tag, 1);
                }
            }
        }
        // 用stream将Map转换为根据出现次数的降序的Map
        return tagsCount.entrySet().stream().
                sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).
                collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    @GetMapping("/mostlike")
    public Result<?> getMostLikes(){
        String jsonStr;
        List<News> news;

        try{
            jsonStr = stringRedisTemplate.opsForValue().get(MOST_LIKES);
        }catch (RedisConnectionFailureException e){
            news = newsService.selectLatestBlogs();
            return Result.success(news);
        }

        if (StrUtil.isBlank(jsonStr)) {
            news = newsService.selectMostLikes();
            stringRedisTemplate.opsForValue().set(MOST_LIKES, JSONUtil.toJsonStr(news));
        }else {
            news = JSONUtil.toBean(jsonStr, new TypeReference<List<News>>() {}, true);
        }
        return Result.success(news);
    }

    @GetMapping("/mostcollect")
    public Result<?> getMostCollect(){
        String jsonStr;
        List<News> news;
        try{
            jsonStr = stringRedisTemplate.opsForValue().get(MOST_FAVORITE);
        }catch (RedisConnectionFailureException e){
            news = newsService.selectLatestBlogs();
            return Result.success(news);
        }
        if (StrUtil.isBlank(jsonStr)) {
            news = newsService.selectMostCollect();
            stringRedisTemplate.opsForValue().set(MOST_FAVORITE, JSONUtil.toJsonStr(news));
        }else {
            news = JSONUtil.toBean(jsonStr, new TypeReference<List<News>>() {}, true);
        }
        return Result.success(news);
    }

    /**
     * 获取博客的推荐度
     * @param blogId 博客id
     * @return rst
     */
    @GetMapping("/recommend/{blogId}")
    public Result<?> getRecommend(@PathVariable Integer blogId){
        LambdaQueryWrapper<Favorite> wrapper = Wrappers.lambdaQuery();
        LambdaQueryWrapper<Visit> wrapper2 = Wrappers.lambdaQuery();
        LambdaQueryWrapper<Like> wrapper3 = Wrappers.lambdaQuery();

        wrapper.eq(Favorite::getBlogId, blogId);
        Long favorCount = favoriteService.count(wrapper);

        wrapper2.eq(Visit::getBlogId, blogId);
        List<Visit> list = visitService.list(wrapper2);
        Integer visitCount = 0;
        for (Visit visit : list) {
            visitCount += visit.getCount();
        }

        wrapper3.eq(Like::getBlogId, blogId);
        Long likeCount = likeService.count(wrapper3);

        Integer userId = Objects.requireNonNull(getUser()).getId();
        wrapper.eq(Favorite::getUserId, userId);
        Favorite favorite = favoriteService.getOne(wrapper);
        wrapper3.eq(Like::getUserId, userId);
        Like like = likeService.getOne(wrapper3);

        JSONObject json = JSONUtil.createObj()
                .put("favorites", favorCount)
                .put("visits", visitCount)
                .put("likes", likeCount);
        if (favorite!=null){
            json.set("isFavor", 1);
        }else{
            json.set("isFavor", 0);
        }
        if (like!=null){
            json.set("isLike", 1);
        }else{
            json.set("isLike", 0);
        }
        return Result.success(json);
    }

    /**
     * 更新浏览记录
     * @param blogId 博客ID
     * @return rst
     */
    @PutMapping("/recommend/visits/{blogId}")
    public Result<?> addVisits(@PathVariable Integer blogId){
        //根据博客ID、用户ID查询是否有浏览记录
        Integer userId = Objects.requireNonNull(getUser()).getId();
        LambdaQueryWrapper<Visit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Visit::getUserId, userId).eq(Visit::getBlogId, blogId);
        Visit visit = visitService.getOne(wrapper);
        if (visit==null) {
            //如果没有访问记录就新增
            Visit v = new Visit(userId, blogId, new Date(), 1);
            visitService.save(v);
        }else{
            //有访问记录就更新时间和次数
            visit.setTime(new Date());
            visit.setCount(visit.getCount()+1);
            visitService.saveOrUpdate(visit);
        }
        return Result.success();
    }

    /**
     * 点赞博客
     * @param id 博客id
     * @return rst
     */
    @PutMapping("/recommend/like/{id}")
    public Result<?> addLike(@PathVariable Integer id){
        Integer userId = Objects.requireNonNull(getUser()).getId();
        Like l = new Like(userId, id);
        likeService.save(l);
        try{
            stringRedisTemplate.delete(MOST_LIKES);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.success();
    }

    /**
     * 取消点赞
     * @param id 博客id
     * @return rst
     */
    @PutMapping("/minus/like/{id}")
    public Result<?> minusLike(@PathVariable Integer id){
        Integer userId = Objects.requireNonNull(getUser()).getId();
        LambdaQueryWrapper<Like> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Like::getBlogId, id);
        wrapper.eq(Like::getUserId, userId);
        likeService.remove(wrapper);
        try{
            stringRedisTemplate.delete(MOST_LIKES);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.success();
    }

    /**
     * 通过id查找博客
     * @param id 博客id
     * @return 单条博客
     */
    @GetMapping("/{id}")
    public Result<?> find(@PathVariable Integer id){
        News news = newsService.getById(id);
        if (news != null){
            news.setTags(tagsService.getTagsByBlodId(id));
            return Result.success(news);
        }else {
            return Result.error("-1","没有这篇文章哦");
        }
    }

    /**
     * 查找含有某个tag的所有博客
     * @param pageNum 页码
     * @param pageSize 页容量
     * @param tag 标签
     * @return 博客s
     */
    @GetMapping("/tag")
    public Result<?> findByTag(@RequestParam(defaultValue = "") Integer pageNum,
                               @RequestParam(defaultValue = "") Integer pageSize,
                               @RequestParam(defaultValue = "") String tag)
    {
        // 如果tag不为空加上，查到null的数据
        tag = URLDecoder.decode(tag);
        Page<News> page = new Page<>();
        long realTotal = 0;
        if (StrUtil.isNotBlank(tag)){
            Page<Tags> tagsPage = tagsService.page(new Page<>(pageNum, pageSize), Wrappers.<Tags>lambdaQuery().eq(Tags::getTag, tag));
            realTotal = tagsPage.getTotal();
            List<Integer> blogIds = new ArrayList<>();
            for (Tags tags : tagsPage.getRecords()) {
                blogIds.add(tags.getBlogId());
            }
            page.setRecords(newsService.listByIds(CollUtil.toCollection(blogIds)));
            page.setTotal(tagsPage.getTotal());
            page.setSize(pageSize);
            page.setCurrent(pageNum);
        }else{
            page = newsService.page(new Page<>(pageNum, pageSize), null);
        }
        if (realTotal!=0){
            page.setTotal(realTotal);
        }
        //将分页数据中博客的标签插入到分页中
        for (News record : page.getRecords()) {
            Integer blogId = record.getId();
            record.setTags(tagsService.getTagsByBlodId(blogId));
        }
        return Result.success(page);
    }

    /**
     * 收藏博客
     * @param userId 用户id
     * @param blogId 博客id
     * @return ret
     */
    @PostMapping("/collect/{userId}/{blogId}")
    public Result<?> collectBlog(@RequestBody(required = false) String folder, @PathVariable Integer userId, @PathVariable Integer blogId){
        if (userId > 0 && blogId > 0) {
            Favorite favorite = new Favorite();
            favorite.setBlogId(blogId);
            favorite.setUserId(userId);

            if (folder == null){
                favorite.setFolderId(0);
            }else{
                Folder one = folderService.getOne(Wrappers.<Folder>lambdaQuery().eq(Folder::getFolder, folder));
                if (one == null){
                    Folder f = new Folder(userId, folder);
                    folderService.save(f);
                    favorite.setFolderId(f.getId());
                }else {
                    favorite.setFolderId(one.getId());
                }
            }
            favorite.setTime(new Date());
            favoriteService.save(favorite);
            try{
                stringRedisTemplate.delete(MOST_FAVORITE);
            }catch (Exception e){
                e.printStackTrace();
            }
            return Result.success();
        }
        return Result.error("-1", "用户或博客不存在");
    }

    /**
     * 删除收藏
     * @param userId 用户id
     * @param blogId 博客id
     * @return ret
     */
    @DeleteMapping("/collect/{userId}/{blogId}")
    public Result<?> deleteCollect(@PathVariable Integer userId, @PathVariable Integer blogId){
        System.out.println(userId+" "+blogId);
        if (userId > 0 && blogId > 0) {
            LambdaQueryWrapper<Favorite> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(Favorite::getBlogId, blogId).eq(Favorite::getUserId, userId);
            favoriteService.remove(wrapper);
            try{
                stringRedisTemplate.delete(MOST_FAVORITE);
            }catch (Exception e){
                e.printStackTrace();
            }
            return Result.success();
        }else{
            return Result.error("-1", "用户或博客不存在");
        }
    }

    /**
     * 获取用户的收藏夹
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 博客s
     */
    @GetMapping("/getCollection")
    public Result<?> showCollection(@RequestParam Integer userId,
                                    @RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize,
                                    @RequestParam(defaultValue = "") String search,
                                    @RequestParam(defaultValue = "") Integer folderId,
                                    @RequestParam(defaultValue = "") String author
    ){
        LambdaQueryWrapper<Favorite> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Favorite::getBlogId).eq(Favorite::getUserId, userId);
        if (folderId != null){
            wrapper.eq(Favorite::getFolderId, folderId);
        }
        List<Object> list = favoriteService.listObjs(wrapper);
        if (list.size() == 0){
            Page<News> page = new Page<>(pageNum, pageSize);
            return Result.success(page);
        }
        StringJoiner ids = new StringJoiner(",");
        for (Object l : list) {
            ids.add(l.toString());
        }

        LambdaQueryWrapper<News> wrapper2 = Wrappers.lambdaQuery();
        wrapper2.inSql(News::getId, ids.toString());
        if (StrUtil.isNotBlank(search)){
            wrapper2.like(News::getTitle, search);
        }
        if (StrUtil.isNotBlank(author)){
            wrapper2.eq(News::getAuthor, author);
        }
        Page<News> page = newsService.page(new Page<>(pageNum, pageSize), wrapper2);
        //将分页数据中博客的标签插入到分页中
        for (News record : page.getRecords()) {
            Integer blogId = record.getId();
            record.setTags(tagsService.getTagsByBlodId(blogId));
        }
        return Result.success(page);
    }

    /** 获取浏览记录
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页容量
     * @return rst
     */
    @GetMapping("/getVisits/{userId}")
    public Result<?> getVisits(@PathVariable Integer userId,
                               @RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String search){
        LambdaQueryWrapper<Visit> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Visit::getBlogId, Visit::getTime).eq(Visit::getUserId, userId);
        Page<Map<String, Object>> page = visitService.pageMaps(new Page<>(pageNum, pageSize), wrapper);
        // 获取分页中的数据
        List<Map<String, Object>> list = page.getRecords();
        // 重写toString函数，便于inSql的执行
        StringJoiner ids = new StringJoiner(",");
        // 分离出博客ID
        for (Map<String, Object> map : list) {
            ids.add(map.get("blog_id").toString());
        }
        LambdaQueryWrapper<News> wrapper2 = Wrappers.lambdaQuery();
        // inSql相当于sql语句中的 Where XXXX in（...）
        wrapper2.inSql(News::getId, ids.toString());
        if (StrUtil.isNotBlank(search)){
            wrapper2.like(News::getTitle, search);
        }
        Page<News> page2 = newsService.page(new Page<>(pageNum, pageSize), wrapper2);
        // 将访问时间替换博客创建时间
        for (int i = 0; i < page2.getRecords().size(); i++) {
            page2.getRecords().get(i).setDate(DateUtil.parse(list.get(i).get("time").toString()));
        }
        //将分页数据中博客的标签插入到分页中
        for (News record : page2.getRecords()) {
            Integer blogId = record.getId();
            record.setTags(tagsService.getTagsByBlodId(blogId));
        }
        return Result.success(page2);
    }

    /**
     * 获取博客归档
     * @return arcs
     */
    @GetMapping("/archive")
    public Result<?> archive(){
        return Result.success(newsService.selectArchive());
    }

    /**
     * 根据关注的某个用户（作者）获取收藏的博客
     * @param author 作者
     * @param pageNum 页码
     * @param pageSize 页容量
     * @param search 标题模糊搜索
     * @return rst
     */
    @PostMapping("/getSub")
    public Result<?> getSub(@RequestBody String author,
                            @RequestParam(defaultValue = "1")Integer pageNum,
                            @RequestParam(defaultValue = "10")Integer pageSize,
                            @RequestParam(defaultValue = "")String search
    ){
        LambdaQueryWrapper<News> wrapper = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(author)){
            wrapper.eq(News::getAuthor, author);
        }
        if (StrUtil.isNotBlank(search)){
            wrapper.like(News::getTitle, search);
        }
        Page<News> page = newsService.page(new Page<>(pageNum, pageSize), wrapper);
        //将分页数据中博客的标签插入到分页中
        for (News record : page.getRecords()) {
            Integer blogId = record.getId();
            record.setTags(tagsService.getTagsByBlodId(blogId));
        }
        return Result.success(page);
    }
}