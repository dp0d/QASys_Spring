package com.exam.qa_robot.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.qa_robot.common.Constants;
import com.exam.qa_robot.controller.dto.LableDto;
import com.exam.qa_robot.entity.Categorys;
import com.exam.qa_robot.exception.ServiceException;
import com.exam.qa_robot.mapper.CorpusMapper;
import com.exam.qa_robot.service.ICategorysService;
import com.exam.qa_robot.service.impl.CategorysServiceImpl;
import io.swagger.models.auth.In;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.rmi.ServerException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exam.qa_robot.common.Result;

import com.exam.qa_robot.service.ICorpusService;
import com.exam.qa_robot.entity.Corpus;
import com.exam.qa_robot.utils.fileToArray;
import com.exam.qa_robot.utils.RestTemplateUtils;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author vaifer
 * @since 2022-06-27
 */
@RestController
@RequestMapping("/corpus")
public class CorpusController {

    @Resource
    private RestTemplateUtils restTemplateUtils;

    @Resource
    private fileToArray fileToArray;
    @Resource
    private ICategorysService categorysService;
    @Resource
    private CorpusMapper corpusMapper;
    @Resource
    private ICorpusService corpusService;

    @Value("${ip.server}")
    private String serverIp;

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${files.upload.ppath}")
    private String PFileUploadPath;



    /**
     * 文件上传接口
     * @param file：前端传来的文件
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file,
                         @RequestParam String category,
                         @RequestParam Integer uid) throws IOException {
        System.out.println("上传文件");
        System.out.println(category);
        System.out.println(uid);

        QueryWrapper<Categorys> wrapper =new QueryWrapper();
        wrapper.eq("category",category);
        Categorys c = categorysService.getOne(wrapper);
        if (c==null){
            throw new ServiceException(Constants.CODE_400,"学科不存在");
        }
        String originalFilename = file.getOriginalFilename();

        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        //定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUid = uuid + StrUtil.DOT + type;
//        String  fileUUid=originalFilename;
        File uploadFile = new File(fileUploadPath + fileUUid);

        //判断配置的文件目录是否存在，不存在则超创建新的目录
        if (uploadFile.getParentFile().exists()) {
            uploadFile.getParentFile().mkdirs();
        }
        String url;
        //上传文件到磁盘
        file.transferTo(uploadFile);
        //获取文件md5
        String md5 = SecureUtil.md5(uploadFile);
        //从数据库查询是否已有相同的记录
        List<Corpus> corpusList = getFileByMd5(md5);


        if (corpusList!=null){//相同文件已存在，直接获取url并删除重复的新文件
            Corpus one = corpusList.get(0);
            url= one.getDocUrl();
//            如果文件名也一致，直接返回url，不再将数据存入数据库
//            if (corpusList.contains(new Corpus(filename))) {
//                return url;
//            }
            uploadFile.delete();
            return Result.success(Constants.CODE_201,url,"文件已存在，请勿重新上传");
        }else {

            url  = serverIp + "/corpus/download/" + fileUUid;


            //将地址存储到数据库
            Corpus corpus = new Corpus();
            corpus.setFilename(originalFilename);
            corpus.setType(type);
            corpus.setSize(size/1024);
            corpus.setMd5(md5);
            corpus.setDocUrl(url);
            corpus.setCid(c.getCid());
            corpus.setUid(uid);
            int insert = corpusMapper.insert(corpus);
            System.out.println(insert);

            QueryWrapper<Corpus> wrapper1= new QueryWrapper();
            wrapper1.eq("doc_url",url);
            Integer fid = corpusService.getOne(wrapper1).getFid();
//        Result.success(corpusService.saveOrUpdate(corpus))
            try {
                restTemplateUtils.getLabel(url, String.valueOf(33));
            } catch (Exception e) {
                return Result.error(Constants.CODE_500,"文件处理服务无响应");
            }

            return Result.success(fid);
        }


    }
    @PostMapping("/pUpload")
    public Result processedUpload(@RequestParam MultipartFile file,
                                  @RequestParam String pType,
                                  @RequestParam Integer fid) throws IOException {
        String originalFilename = file.getOriginalFilename();
        System.out.println(pType+fid+"-------------------------------------------");
        System.out.println(originalFilename);

        File uploadFile = new File(PFileUploadPath + originalFilename);

        //判断配置的文件目录是否存在，不存在则超创建新的目录
        if (uploadFile.getParentFile().exists()) {
            uploadFile.getParentFile().mkdirs();
        }

        QueryWrapper<Corpus> wrapper = new QueryWrapper();
        wrapper.eq("fid",fid);
        Corpus one = corpusService.getOne(wrapper);
        if (one==null){
            throw new ServiceException(Constants.CODE_400,"原始文件不存在");
        }
        // 写文件的本地路径
        if (pType.equals("r")){
            one.setRurl(PFileUploadPath + originalFilename);
        }else if (pType.equals("s")){
            one.setSurl(PFileUploadPath + originalFilename);
        }else if (pType.equals("d")){
            one.setDurl(PFileUploadPath + originalFilename);
        }else {
            throw new ServiceException(Constants.CODE_400,"不存在的文件类型");
        }

        corpusService.saveOrUpdate(one);
        //最后保存文件
        file.transferTo(uploadFile);
        return Result.success();
    }

    /**
     * 从数据库中根据md5查询是否有重复信息，并返回其中的第一条
     * @param md5
     * @return
     */
    private List<Corpus> getFileByMd5(String md5) {
        QueryWrapper<Corpus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Corpus> corpusList = corpusMapper.selectList(queryWrapper);
        return corpusList.size() == 0 ? null : corpusList;
    }

    /**\
     * 文件下载接口 http://localhost:9090/corpus/{fileUUid}
     * @param fileUUid
     * @param response
     * @throws IOException
     */
    @GetMapping("/download/{fileUUid}")
    public void download(@PathVariable String fileUUid, HttpServletResponse response) throws IOException {
        //根据文件的唯一标识码获取文件
        File downloadFile = new File(fileUploadPath + fileUUid);
        //设置输出流的格式
        ServletOutputStream os = response.getOutputStream();

        response.addHeader("Content-Disposiion","attachment;filename="+ URLEncoder.encode(fileUUid,"UTF-8"));
        response.setContentType("application/octet-stream");
        //读取文件字节流
        os.write(FileUtil.readBytes(downloadFile));
        os.flush();
        os.close();
    }

    @PostMapping("/getLabel")
    public Result getLabel(@RequestParam Integer fid){
        //丛数据库中获取文件地址
        QueryWrapper<Corpus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fid",fid);
        Corpus one = corpusService.getOne(queryWrapper);
        if (one==null){
            throw new ServiceException(Constants.CODE_400,"未上传的文件");
        }
        String r_url = one.getRurl();
        if (r_url==null){
            throw new ServiceException(Constants.CODE_500,"未处理的文件");
        }

        //根据文件地址读取并转类型
        List<LableDto> lableDtos = fileToArray.fileRead(r_url);
        return  Result.success(lableDtos);
    }
    @PostMapping("/setLabel")
    public Result setLabel(@RequestBody LableDto lableDto){

        QueryWrapper<Corpus> wrapper =new QueryWrapper<>();
        wrapper.eq("fid",lableDto.getId());
        Corpus one = corpusService.getOne(wrapper);
        if (one==null)return Result.error(Constants.CODE_400,"文件不存在");
        fileToArray.fileWriter(one.getRurl(),lableDto.getKeys(),lableDto.getLabels());



        return  Result.success();
    }


    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        return  Result.success(corpusService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        return  Result.success(corpusService.removeBatchByIds(ids));
    }

    @GetMapping
    public Result findAll(){
        return  Result.success(corpusService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){
        return Result.success(corpusService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam Integer uid) {
        //----------------------------------------------------------------------------待补充
        QueryWrapper<Corpus> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("fid");
        return Result.success(corpusService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }




}

