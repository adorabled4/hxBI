package com.dhx.bi.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author adorabled4
 * @className ExcelUtils
 * @date : 2023/07/05/ 10:38
 **/
@Slf4j
public class ExcelUtils {


    /**
     * excel to csv
     *
     * @param multipartFile 多部分文件
     * @return {@link String}
     */
    public static String excel2CSV(MultipartFile multipartFile) {
        List<LinkedHashMap<Integer,String> > list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream()).excelType(ExcelTypeEnum.XLS)
//            list = EasyExcel.read(ResourceUtils.getFile("classpath:data.xls")).excelType(ExcelTypeEnum.XLS)
                    .sheet()
                    .headRowNumber(1) // 前面空出几行的head
                    .doReadSync();
        } catch (IOException e) {
            log.error("excel handle error",e);
        }
        StringBuilder sb = new StringBuilder("");
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = list.get(0);
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        sb.append(StringUtils.join(headerList,",")).append("\n");
        // 读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            sb.append(StringUtils.join(dataList,",")).append("\n");
        }
        return sb.toString();
    }

}