package com.example.demo.common;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @author Geed
 */
public class FastGenerateCodes {
//    public static void main(String[] args) {
//        FastAutoGenerator.create("jdbc:mysql://localhost:3306/springboot-vue?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2b8",
//                "root", "root")
//                .globalConfig(builder -> {
//                    builder.author("Geed") // 设置作者
////                            .enableSwagger() // 开启 swagger 模式
//                            .fileOverride() // 覆盖已生成文件
//                            .outputDir("D://MybaitsPlusTest"); // 指定输出目录
//                })
//                .packageConfig(builder -> {
//                    builder.parent("com.example.demo") // 设置父包名
////                            .moduleName("system") // 设置父包模块名
//                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D://MybaitsPlusTest//xml")); // 设置mapperXml生成路径
//                })
//                .strategyConfig(builder -> {
//                    builder.addInclude("folder") // 设置需要生成的表名
//                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
//                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .execute();
//    }
}