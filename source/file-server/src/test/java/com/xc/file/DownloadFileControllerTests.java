package com.xc.file;

import com.xc.tool.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p></p>
 *
 * @author xc
 * @version v1.0.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
public class DownloadFileControllerTests {

    static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        download1();
        download2();
        download3();
        download4();
        download5();
        download6();
        download7();
    }

    public static void download1() {
        String filePath = "F:\\课程视频\\国际贸易理论与实务";
        Map<String, String> map = new HashMap<>();
        map.put("课程导读", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/e957bf525285890805718723483/n7Awof6xp5wA.mp4");
        map.put("第一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4031cc865285890792596909695/7JL5mdfA1JkA.mp4");
        map.put("第一章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bcc20d2b5285890781253710933/E3R787aARccA.mp4");
        map.put("第二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0e65b55285890792529815434/KqsBoUOAUYgA.mp4");
        map.put("第二章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bcc20dac5285890781253710970/nOsaecywU6gA.mp4");
        map.put("第二章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b9fad3ae5285890781246841344/Z3e2GSMurXYA.mp4");
        map.put("第三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4031d8405285890792596909914/GPFGE9an60QA.mp4");
        map.put("第三章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/c17e77ba5285890781253920999/auaVY3cAjAUA.mp4");
        map.put("第三章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/c17edca85285890781253921008/aiVslFnfqd8A.mp4");
        map.put("第三章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/f908290c5285890781247251094/P5Kkw5pDuVcA.mp4");
        map.put("第四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0e69d25285890792529815521/OoGQztG2daoA.mp4");
        map.put("第四章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/f9082ce65285890781247251160/HRYa075UbuIA.mp4");
        map.put("第四章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/6e4a941c5285890781768654248/0LmvwIwMrNIA.mp4");
        map.put("第五章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0e6e5c5285890792529815648/ljwjeDzCXNkA.mp4");
        map.put("第五章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/6e4a97d75285890781768654306/OltN4G9LakgA.mp4");
        map.put("第五章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/5aa7ac4e5285890781744323382/883FuMT6WlUA.mp4");
        map.put("第六章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403edfb85285890792596910149/C0MXEtIbJAwA.mp4");
        map.put("第六章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403edfb85285890792596910149/C0MXEtIbJAwA.mp4");
        map.put("第六章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/6e4a985d5285890781768654348/St67Nd9rH6AA.mp4");
        map.put("第七章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0e725c5285890792529815729/TMwC8EFdABMA.mp4");
        map.put("第七章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/6e4a98df5285890781768654386/FCmBt03EemgA.mp4");
        map.put("第七章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/757816085285890786143998995/1G5Ngsbd8ckA.mp4");
        map.put("第七章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/5aa7b02e5285890781744323454/x46UgzYoAkUA.mp4");
        map.put("第八章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403ee4375285890792596910265/aIA5Svu7RAwA.mp4");
        map.put("第八章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/5f786bcf5285890781744547600/CvwHvO04teQA.mp4");
        map.put("第八章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/afe59d9d5285890781769198353/qdXI7691bHcA.mp4");
        map.put("第八章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/5f786cb85285890781744547672/Fl6itjMzvbwA.mp4");
        map.put("第九章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0e76575285890792529815805/AEtJqFOx194A.mp4");
        map.put("第九章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/5f7870145285890781744547704/bbSamtVAskUA.mp4");
        map.put("第九章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/afe5a17a5285890781769198422/eQQti5zXvKcA.mp4");
        map.put("第九章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/afe5a19e5285890781769198435/8VtDURhAah4A.mp4");
        map.put("第十章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403ee7d35285890792596910315/Sx2rGFpqKNkA.mp4");
        map.put("第十章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/5f78711d5285890781744547785/Q5ziGp6CEmoA.mp4");
        map.put("第十章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/5f78713a5285890781744547791/prWOzSfbvxgA.mp4");
        map.put("第十一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0e7afb5285890792529815935/iarNusKrD3UA.mp4");
        map.put("第十一章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f6a795285890781769710368/xQQzff3jB1QA.mp4");
        map.put("第十一章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/5f7875195285890781744547862/Fi3E3Kdh2q0A.mp4");
        map.put("第十一章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/afe5a6a05285890781769198590/EpVFQH1qsGcA.mp4");
        map.put("第十一章-第五节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/5f7875615285890781744547888/q42bE6ef6z0A.mp4");
        map.put("第十二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403ef0365285890792596910507/bDVO2skQIJwA.mp4");
        map.put("第十二章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f6e315285890781769710423/b6vJrYmOXaQA.mp4");
        map.put("第十二章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f6e705285890781769710440/F3lcNzdAgYsA.mp4");
        map.put("第十二章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f6ebb5285890781769710469/HMnlMOcU4iAA.mp4");
        map.put("第十三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403ef11d5285890792596910577/Rp6ZDwQwyBoA.mp4");
        map.put("第十三章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/68f264dc5285890781744969902/4LAsHgiRsFIA.mp4");
        map.put("第十三章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f6f185285890781769710493/cfuykOFib1sA.mp4");
        map.put("第十三章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f6f1d5285890781769710498/BavaA2ocq20A.mp4");
        map.put("第十四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0ee5755285890792529816145/t2qzPzcnkX8A.mp4");
        map.put("第十四章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f724f5285890781769710511/AfzU6KzFCPsA.mp4");
        map.put("第十四章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/68f265815285890781744969952/tcyAHW5ltJEA.mp4");
        map.put("第十五章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0ee5d95285890792529816176/Rve5OcZwzT8A.mp4");
        map.put("第十五章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f72b35285890781769710542/FAoiK1xDtOkA.mp4");
        map.put("第十六章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0f7e3a5285890792529817419/Sxl76t5JDAwA.mp4");
        map.put("第十六章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/68ff67a95285890781744970000/ia9FaCzMaF4A.mp4");
        map.put("第十六章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f733a5285890781769710585/Ac0m8UzXWt4A.mp4");
        map.put("第十六章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/bd4f76745285890781769710606/duEXaP8CtVYA.mp4");
        map.put("第十六章-第五节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/68ff68565285890781744970058/GYYTfZDZLSUA.mp4");
        System.out.println(map.size());
        downloadFile(map, filePath);
    }

    public static void download2() {
        String filePath = "F:\\课程视频\\企业经营战略";
        Map<String, String> map = new HashMap<>();
        map.put("课程导读", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/228277615285890797724484766/Avy7yf1A7hYA.mp4");
        map.put("第一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/5ba737d85285890795426691571/AVR1bRjA188A.mp4");
        map.put("第一章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/c64a67065285890795407221034/uaGqSwATNdQA.mp4");
        map.put("第一章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/c64a676b5285890795407221066/g3shZAbVd8gA.mp4");

        map.put("第二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/c64a6ae65285890795407221106/CrLAdkDsVggA.mp4");
        map.put("第二章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/5ba73c3d5285890795426691684/CaqVNhBcuHwA.mp4");
        map.put("第二章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/5ba73ff75285890795426691741/gkd6ozWVvsIA.mp4");

        map.put("第三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/5ba7407e5285890795426691784/7EFYpaHb8GEA.mp4");
        map.put("第三章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/5ba743b95285890795426691806/ZbpCrKAHRU4A.mp4");
        map.put("第三章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/c64a6fc65285890795407221250/Re7Mql6HegYA.mp4");

        map.put("第四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/5ba744bc5285890795426691881/OCEaCJAdeaYA.mp4");
        map.put("第四章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/5ba7481a5285890795426691915/vht3TSIyKC0A.mp4");

        map.put("第五章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c0fb8a5285890796668211020/iOJvVAUHMGQA.mp4");
        map.put("第五章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c0fc745285890796668211093/qJz0y61nTE0A.mp4");
        map.put("第五章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c0ffaf5285890796668211115/9mdw6wUVxqwA.mp4");

        map.put("第六章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c0fff55285890796668211139/IAvuqq8f8dMA.mp4");
        map.put("第六章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468b3e5c5285890796667677005/4UodVDQWcNwA.mp4");
        map.put("第六章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468b3ea05285890796667677027/yCusC8c25zwA.mp4");
        map.put("第六章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c1ae565285890796668212931/EAAlVOHtJZIA.mp4");
        map.put("第六章-第五节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468ad0275285890796667676737/2IzCkLwaQ5QA.mp4");

        map.put("第七章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c104105285890796668211224/xvDnAoBD6PgA.mp4");
        map.put("第七章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468ad0e95285890796667676793/YZ0LOyDVpEkA.mp4");
        map.put("第七章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c104ba5285890796668211279/pav8ypzOaeQA.mp4");

        map.put("第八章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c104d95285890796668211287/2EDPgbFwarIA.mp4");
        map.put("第八章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468ad4645285890796667676833/nfDaNgPec0cA.mp4");
        map.put("第八章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c108f85285890796668211376/28nmDHaxp7gA.mp4");
        map.put("第八章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468ad8a45285890796667676932/XEaikAJoU00A.mp4");
        map.put("第八章-第五节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c10cd95285890796668211449/jt38NCqXvxcA.mp4");

        map.put("第九章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468ad90d5285890796667676968/Wdhxcb9BFA0A.mp4");
        map.put("第九章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468b3f215285890796667677064/evuBDWspx8AA.mp4");
        map.put("第九章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c111bb5285890796668211595/FXVejTQRRbYA.mp4");
        map.put("第九章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/85285cda5285890796668020938/aApcAOmhH3kA.mp4");
        map.put("第九章-第五节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c115755285890796668211652/a6uLIm06bxYA.mp4");
        map.put("第九章-第六节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c119315285890796668211711/V8riJmILj2AA.mp4");

        map.put("第十章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c119525285890796668211721/pOydcrBrOWAA.mp4");
        map.put("第十章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c1197b5285890796668211739/flECNGzqsAcA.mp4");
        map.put("第十章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c119dd5285890796668211768/ctsKPfxuUKwA.mp4");

        map.put("第十一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c1ae9e5285890796668212957/8y6UgIwrbXkA.mp4");
        map.put("第十一章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c1af1d5285890796668212992/fofxTkPa1egA.mp4");
        map.put("第十一章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c214f15285890796668213070/rPFRd0pULDcA.mp4");

        map.put("第十二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c218515285890796668213106/QExTBSXOSGUA.mp4");
        map.put("第十二章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c218915285890796668213124/HOd97XHjV8QA.mp4");
        map.put("第十二章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468bd8a45285890796667678390/s5inXnJ48AIA.mp4");
        map.put("第十二章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468bdc245285890796667678435/8tzSSsf3688A.mp4");

        map.put("第十三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468bdca45285890796667678471/WNtQCw34bRAA.mp4");
        map.put("第十三章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/468be0675285890796667678537/z4rxDBZf06kA.mp4");
        map.put("第十三章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/89c21d595285890796668213267/mLQ18KpANLQA.mp4");

        map.put("第十四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/2238f7f35285890797724442632/9RJmVgixoRUA.mp4");
        map.put("第十四章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/2238f8bb5285890797724442694/RUsW4kLsIOAA.mp4");
        System.out.println(map.size());
        downloadFile(map, filePath);
    }

    public static void download3() {
        String filePath = "F:\\课程视频\\组织行为学";
        Map<String, String> map = new HashMap<>();
        map.put("课程导读", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/bc95333e5285890788090824958/VJc2avIKtU8A.mp4");
        map.put("绪论-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4041a1f85285890792596915252/zQLNrpRgtvcA.mp4");
        map.put("绪论-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b56437715285890781246654138/TIQit74NussA.mp4");
        map.put("绪论-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b564378f5285890781246654145/x5sJv6hURMsA.mp4");
        map.put("第一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4041aa7b5285890792596915453/8s8QPgfgYawA.mp4");
        map.put("第一章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6ef8ab5285890781253614264/GzlAeAPibWwA.mp4");
        map.put("第一章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6ef8cd5285890781253614275/q27QWFxFiT0A.mp4");
        map.put("第二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4041bb5e5285890792596915843/XaUawyAA55QA.mp4");
        map.put("第二章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6ef8ee5285890781253614285/jATzypcvTdoA.mp4");
        map.put("第二章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b5643b6b5285890781246654213/AZTLwoghZuUA.mp4");
        map.put("第二章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b5643b8b5285890781246654222/cbYKsnCopuUA.mp4");
        map.put("第二章-第五节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b5643baf5285890781246654235/qah6V2Nl1OEA.mp4");
        map.put("第三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af58f4bc5285890792529859316/hpZIr4dsnnEA.mp4");
        map.put("第三章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6efc8e5285890781253614339/DSA1pcXmRM8A.mp4");
        map.put("第三章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b5643c585285890781246654289/AeqVMP3EeYoA.mp4");
        map.put("第四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/40423aff5285890792596916546/5T4jMPyZ0YYA.mp4");
        map.put("第四章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b5643f8b5285890781246654303/Ap4qga5V498A.mp4");
        map.put("第四章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b5643faf5285890781246654316/nc6LHvakNQgA.mp4");
        map.put("第四章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b5643fcd5285890781246654323/gxYAP8vEIhYA.mp4");
        map.put("第五章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/40424c215285890792596916953/SYiLO6grijIA.mp4");
        map.put("第五章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6f00685285890781253614405/aXiAnOeWMEEA.mp4");
        map.put("第六章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af6615765285890792529860129/sdovyMaDD1YA.mp4");
        map.put("第六章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b56440525285890781246654364/PW9HaS3eLvYA.mp4");
        map.put("第七章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4042cfc15285890792596917736/BsVYk0mJOz0A.mp4");
        map.put("第七章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b56440b35285890781246654392/pRhMoZWwuXwA.mp4");
        map.put("第七章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b56443cf5285890781246654406/TiJ2bsDuGi8A.mp4");
        map.put("第八章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af669db65285890792529861038/kbQztqpHiuIA.mp4");
        map.put("第八章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6f018d5285890781253614491/VcNIVyaImKsA.mp4");
        map.put("第八章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6f04ad5285890781253614509/x0yuUnA4DVIA.mp4");
        map.put("第九章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/40434b3b5285890792596918342/3vklwwG9vkwA.mp4");
        map.put("第九章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b56444985285890781246654469/hqk2hO5czA0A.mp4");
        map.put("第十章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/404357815285890792596918609/7p9AzQCcZvEA.mp4");
        map.put("第十章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6f05725285890781253614568/ruwpyEwvY7wA.mp4");
        map.put("第十章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6f05915285890781253614576/12VbdZDu2PkA.mp4");
        map.put("第十一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af66c05f5285890792529861884/OZ7SHbjVToAA.mp4");
        map.put("第十一章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6f05d25285890781253614595/pvt2AvAn2x4A.mp4");
        map.put("第十一章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6f08ec5285890781253614607/AWnsECifkdoA.mp4");
        System.out.println(map.size());
        downloadFile(map, filePath);
    }

    public static void download4() {
        String filePath = "F:\\课程视频\\质量管理学";
        Map<String, String> map = new HashMap<>();
        map.put("课程导读", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8d6174d5285890786155820441/hoTf5sJrDcsA.mp4");
        map.put("第一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4043ceda5285890792596919124/NvLeKxNsmUsA.mp4");
        map.put("第一章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f243d1ba5285890786155581918/Ifb6h1I1uKgA.mp4");
        map.put("第一章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f243d21e5285890786155581949/tTObj4kvkB4A.mp4");
        map.put("第一章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f243d29e5285890786155581985/o28bva2FWi8A.mp4");
        map.put("第二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4043d3dd5285890792596919280/55u1RItod2cA.mp4");
        map.put("第二章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f243d2bf5285890786155581995/KEsUze1C9aYA.mp4");
        map.put("第二章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f24437cc5285890786155582012/ufrpxX9Dqp4A.mp4");
        map.put("第三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4043dfbc5285890792596919513/qKoVQkIfqqsA.mp4");
        map.put("第三章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f244386f5285890786155582060/eC2mhVBTEIsA.mp4");
        map.put("第三章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/211253bc5285890786142639839/CgPtKnPpfpIA.mp4");
        map.put("第三章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/211253fd5285890786142639858/3yYTB6h95SoA.mp4");
        map.put("第四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4043e4c15285890792596919671/JjSUxsNvzfYA.mp4");
        map.put("第四章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f2443c745285890786155582146/XCuQouxDKowA.mp4");
        map.put("第四章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/211254805285890786142639897/6SjqBsZpIkYA.mp4");
        map.put("第五章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af673fbf5285890792529862568/o0vEm9NyEBAA.mp4");
        map.put("第五章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693c60f25285890786143416664/za0vZA0UrEMA.mp4");
        map.put("第五章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693c61125285890786143416673/ur3ta7FRrRAA.mp4");
        map.put("第五章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8e65e5285890786155819297/rCPcKIiA200A.mp4");
        map.put("第六章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4043f0a15285890792596919905/vVnIYVzRMbIA.mp4");
        map.put("第六章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8ea145285890786155819350/NYmqepi1TNYA.mp4");
        map.put("第六章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8ea395285890786155819364/8UawAxFaewcA.mp4");
        map.put("第六章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693c64cf5285890786143416733/XEl1VzrrH44A.mp4");
        map.put("第七章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4063b3045285890792596931698/oS7RJcdcIqoA.mp4");
        map.put("第七章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693c64f65285890786143416749/HQGI1KKZIqoA.mp4");
        map.put("第七章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693c65515285890786143416771/DZ5hhVWlfuUA.mp4");
        map.put("第七章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693c65585285890786143416778/YaomRIvQpxgA.mp4");
        map.put("第七章-第五节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8edf75285890786155819425/WFvVUI87ZesA.mp4");
        map.put("第八章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4063b6775285890792596931730/CcCXaGEpIzIA.mp4");
        map.put("第八章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693c68b35285890786143416809/Zx9rAS109tgA.mp4");
        map.put("第八章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693c68ed5285890786143416821/GbVSqnE8sogA.mp4");
        map.put("第九章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4063b6fb5285890792596931770/kHohVDxVFvoA.mp4");
        map.put("第九章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8eedb5285890786155819492/O99WWE7XhqAA.mp4");
        map.put("第九章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693c694f5285890786143416850/FcUfEA8ZDJ0A.mp4");
        map.put("第十章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4063b7445285890792596931797/KapXR5z5VOoA.mp4");
        map.put("第十章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8f23a5285890786155819527/lWWQcZAyrLsA.mp4");
        map.put("第十一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4063bb215285890792596931866/c0Y2F1xmLj0A.mp4");
        map.put("第十一章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8f67d5285890786155819629/tfju9JzNwxIA.mp4");
        map.put("第十一章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8f6ff5285890786155819667/amR4unqTdN4A.mp4");
        map.put("第十二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4063bfc35285890792596931994/hFfduV5LsA4A.mp4");
        map.put("第十二章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693cd4345285890786143417098/Jp7xgVLLvdgA.mp4");
        map.put("第十二章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8fa9c5285890786155819718/Bm3WDJRV1EkA.mp4");
        map.put("第十二章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/cef738ef387702297521069204/17Xb6yE6cQwA.mp4");
        map.put("第十三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/406424d65285890792596932017/QkrAqNzCNEsA.mp4");
        map.put("第十三章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/900b4b9d387702297520674289/zjKTFLMT2E0A.mp4");
        map.put("第十三章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f8c8febd5285890786155819809/bQPeuezGQUcA.mp4");
        map.put("第十三章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693cdbed5285890786143417235/O8pNDIvCubcA.mp4");
        map.put("第十四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af9e90dd5285890792529894156/1xPBXfl0iFMA.mp4");
        map.put("第十四章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/693cdcb05285890786143417292/7KOcoNqiDncA.mp4");
        System.out.println(map.size());
        downloadFile(map, filePath);
    }

    public static void download5() {
        String filePath = "F:\\课程视频\\管理学原理";
        Map<String, String> map = new HashMap<>();
        map.put("课程导读", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3393ea035285890790260084014/idTybbcdnnEA.mp4");
        map.put("第一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0ba7ac5285890792529810421/4MR4SW4tUm0A.mp4");
        map.put("第一章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b8998f485285890781253586100/iSY61twaDmsA.mp4");
        map.put("第一章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b8998f8d5285890781253586123/WqGd4cjEibMA.mp4");
        map.put("第二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403027045285890792596906669/kvAlH6YkFQoA.mp4");
        map.put("第二章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b8998fcd5285890781253586141/tuvAC02M8PQA.mp4");
        map.put("第二章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b8998ff25285890781253586155/SditwIHJdDoA.mp4");
        map.put("第二章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89990115285890781253586163/kpjWAFqsm2EA.mp4");
        map.put("第二章-第五节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b3466c725285890781246586009/998fmqnhghsA.mp4");
        map.put("第三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0babd45285890792529810519/TdTJj4EIKPYA.mp4");
        map.put("第三章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89990525285890781253586182/pEaXcTBGMa0A.mp4");
        map.put("第三章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89990715285890781253586190/fFEdpIWOhxgA.mp4");
        map.put("第四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/40302a985285890792596906711/utq0xqUZn9YA.mp4");
        map.put("第四章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89993925285890781253586209/Vq1tadJHOLgA.mp4");
        map.put("第五章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/40302ae15285890792596906738/vt0hVAN1YAQA.mp4");
        map.put("第五章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89993b35285890781253586219/RHqD82eB8agA.mp4");
        map.put("第五章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89993d05285890781253586225/8edAndWD3h4A.mp4");
        map.put("第六章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/40302b5f5285890792596906772/lUXvishG4cAA.mp4");
        map.put("第六章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b346712e5285890781246586140/BGFrCqX5At4A.mp4");
        map.put("第六章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b34671345285890781246586146/EF4metp8AscA.mp4");
        map.put("第七章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/40302f215285890792596906837/D8gS4phEHKYA.mp4");
        map.put("第七章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b34671975285890781246586176/AoGJ4ZB5QeEA.mp4");
        map.put("第七章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89997eb5285890781253586310/90NWfA0cj4cA.mp4");
        map.put("第八章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0bb0f45285890792529810681/f8uwXdt5EeoA.mp4");
        map.put("第八章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b34675715285890781246586242/3IbEKLG5DRYA.mp4");
        map.put("第九章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/40302fc35285890792596906884/RUsFwh2XlwQA.mp4");
        map.put("第九章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89998935285890781253586363/tOda0yGz3zYA.mp4");
        map.put("第九章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89998b15285890781253586370/MAVxaxNn7bAA.mp4");
        map.put("第十章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4030333b5285890792596906921/EaRw6XFu3CcA.mp4");
        map.put("第十章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b89998b95285890781253586378/oTAtv86JVx4A.mp4");
        map.put("第十章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b346761b5285890781246586297/GhreTJw6zAYA.mp4");
        map.put("第十一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4030339e5285890792596906951/0aq2zuWoOggA.mp4");
        map.put("第十一章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b34679345285890781246586308/hq2O6xvCF6EA.mp4");
        map.put("第十一章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b8999c6e5285890781253586430/eNgzTluwIAEA.mp4");
        map.put("第十二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403033c15285890792596906963/inHA51ikaVkA.mp4");
        map.put("第十二章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b3467a185285890781246586375/AjUPusmcJT8A.mp4");
        map.put("第十二章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b8999cf45285890781253586472/c1oEqMNOHI0A.mp4");
        map.put("第十二章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b8999cfb5285890781253586479/z8WOVyuWzCwA.mp4");
        map.put("第十三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0bb93a5285890792529810867/s7aieBdTVhAA.mp4");
        map.put("第十三章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b3467d905285890781246586412/YgsfBI1qFBcA.mp4");
        map.put("第十三章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b3467daf5285890781246586420/ABXOtwOG2LEA.mp4");
        map.put("第十三章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b899a0725285890781253586515/4i2xXs1JQQ8A.mp4");
        System.out.println(map.size());
        downloadFile(map, filePath);
    }

    public static void download6() {
        String filePath = "F:\\课程视频\\英语二";
        Map<String, String> map = new HashMap<>();
        map.put("第一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/aefe833c5285890792529809555/DFHHSgSDxbYA.mp4");
        map.put("第二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/aefe83a35285890792529809589/sQrHepLFaDwA.mp4");
        map.put("第二章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/644bcc907447398155080882840/SjyeUuj9YHIA.mp4");
        map.put("第二章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/92f6f67b7447398155077898505/k2aiFtNjqcAA.mp4");
        map.put("第二章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/94a4d7087447398155077900304/38Ah6e6DKK8A.mp4");
        map.put("第二章-第五节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/3538274a7447398155076131239/oFA7HpS5X9IA.mp4");
        map.put("第二章-第六节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/354a5e527447398155076141993/ePneVfBuxVsA.mp4");
        map.put("第三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/aefe83c35285890792529809598/IZdgxiOGmBsA.mp4");
        map.put("第三章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ccb7a5e77447398155078019130/B5hPAdrPyrwA.mp4");
        map.put("第四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/aefe873f5285890792529809639/EaQSBIpZVRsA.mp4");
        map.put("第四章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/37e853147447398155076282179/0alCDvhBbmcA.mp4");
        map.put("第五章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/402fa2575285890792596905801/AZJgPvQUexUA.mp4");
        map.put("第五章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/9e7ac0ad7447398155081066752/hkkzkbLIrVcA.mp4");
        map.put("第五章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/d172fb0e7447398155078227226/9aTVvPXU2MQA.mp4");
        map.put("第六章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/402fa2da5285890792596905840/9F21aWsUjzwA.mp4");
        map.put("第六章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/3a3d8bcd7447398155076382504/dp4U31eHFVcA.mp4");
        map.put("第六章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/3a3e26197447398155076383893/TMnaw5BmZZQA.mp4");
        map.put("第七章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/402fa31f5285890792596905863/LB9vRAtUCp0A.mp4");
        map.put("第七章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/d3ece61a7447398155078348387/X3Wj5HwiyDEA.mp4");
        map.put("第七章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/a106b2d37447398155081197483/QdC6wpuBnuQA.mp4");
        map.put("第八章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/402fa3835285890792596905894/a1Gi7hWTrRoA.mp4");
        map.put("第八章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/d664b33b7447398155078465919/sNFR7MplHWwA.mp4");
        map.put("第八章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/a34aec747447398155081289860/XYnF4aj9eIwA.mp4");
        map.put("第九章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/402fa7035285890792596905939/ORmCP1k7lj0A.mp4");
        map.put("第九章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/3ec3174d7447398155076561316/wgtou5dTkT4A.mp4");
        map.put("第九章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/3e9f7fef7447398155076542652/kZxA0O7NikAA.mp4");
        map.put("第十章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/402fa7625285890792596905965/c1fF1NfwKfwA.mp4");
        map.put("第十章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/d8dbcb5c7447398155078581597/B2MAv8eEue8A.mp4");
        map.put("第十章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/412a6e377447398155076671855/aKh384Bax94A.mp4");
        map.put("第十一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/402fa7c45285890792596905994/dvl2nS9MSOIA.mp4");
        map.put("第十一章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/411969967447398155076663709/gCncFwj9HokA.mp4");
        map.put("第十一章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/a79a32737447398155081438755/watRsVIHeisA.mp4");
        map.put("第十二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/34fc81e85285890804251713249/tIy4PuuY9jUA.mp4");
        map.put("第十二章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/a9dd29087447398155081528150/BEazlNKloEgA.mp4");
        map.put("第十二章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/4558afce7447398155076806054/D5aAl9NtmoAA.mp4");
        map.put("第十三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/aefe90415285890792529809875/DRiYN54X3DUA.mp4");
        map.put("第十三章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/4817bdec7447398155076961401/PLGkVW2uCu0A.mp4");
        map.put("第十三章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/4805b2d57447398155076951753/HaNe6AA8ErEA.mp4");
        map.put("第十四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/aefe93965285890792529809900/yFYVxa0cmfgA.mp4");
        map.put("第十四章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/4807c66f7447398155076955041/2eIM0ygrAaUA.mp4");
        map.put("第十四章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/e1c56a327447398155078924055/1w21cKdzTvQA.mp4");
        map.put("第十五章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/40300d7c5285890792596906067/aKtN9RwbgOkA.mp4");
        map.put("第十五章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ae83312d7447398155081720782/SWcfNPEd6RIA.mp4");
        map.put("第十五章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/801639ea7447398155077066006/6AaWdNSDCMEA.mp4");
        map.put("第十五章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/1bf1c2307447398155079103747/zo9Tg4uxJIMA.mp4");
        map.put("第十六章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/4030111a5285890792596906119/F7Nhvbb2lHQA.mp4");
        map.put("第十六章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b11279f67447398155081857780/nj923CtwiyEA.mp4");
        map.put("第十六章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/8454926f7447398155077207392/vfP0ZNMA1BcA.mp4");
        map.put("第十六章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b31eb38f7447398155081916732/9904bMxlsi4A.mp4");
        map.put("第十七章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0b96895285890792529810013/kN2p7tktS9gA.mp4");
        map.put("第十七章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b39d45d27447398155081986152/wCPaP2zAtKcA.mp4");
        map.put("第十七章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b39b0f897447398155081982012/1EUh8DNvk5wA.mp4");
        map.put("第十七章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/eb62dbe67447398155082056511/8slIaqrjc48A.mp4");
        map.put("第十八章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0b96d05285890792529810038/8jYOkexLJ1gA.mp4");
        map.put("第十八章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/eb60b3d07447398155082052786/GujIbnqdkHwA.mp4");
        map.put("第十八章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ed5e05c37447398155082107142/VwGIwEAtOhMA.mp4");
        map.put("第十八章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/8b51638f7447398155077502880/Ao4bS7rNV6MA.mp4");
        map.put("第十九章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0b972b5285890792529810060/ePtmRoY1g3QA.mp4");
        map.put("第十九章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/edefde4d7447398155082189043/BJSythYBdHoA.mp4");
        map.put("第十九章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/8da5dda67447398155077601070/708jrcWUlzcA.mp4");
        map.put("第二十章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0b9ac75285890792529810110/2Ic12BinIEYA.mp4");
        map.put("第二十章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/f02fc9ed7447398155082273904/2YOahwBS6akA.mp4");
        map.put("第二十章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/9030d02e7447398155077730372/B0nNerRyatYA.mp4");
        map.put("第二十章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/f229df277447398155082322624/nAwHkKmxcoUA.mp4");
        map.put("第二十一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403019b65285890792596906322/QZGcASMiGioA.mp4");
        map.put("第二十一章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/2a720dd67447398155079778303/lYlVqWuex8AA.mp4");
        map.put("第二十一章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/f2a7fe4b7447398155082391622/U8taBXpXPbcA.mp4");
        map.put("第二十一章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/f2aa1eaa7447398155082395212/Tne5o5OqAQsA.mp4");
        System.out.println(map.size());
        downloadFile(map, filePath);
    }

    public static void download7() {
        String filePath = "F:\\课程视频\\金融理论与务实";
        Map<String, String> map = new HashMap<>();
        map.put("课程导读", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cae14165285890792529056591/GrJDyDrriicA.mp4");
        map.put("第一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0f82585285890792529817507/lKx01xuEaokA.mp4");
        map.put("第一章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b554edd25285890781246649531/ZeSzkasITUQA.mp4");
        map.put("第一章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cabe2365285890792529052568/b9dgeAfHxHYA.mp4");
        map.put("第二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403efdb85285890792596910860/aISAp7DyYrwA.mp4");
        map.put("第二章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b97dc715285890792596716201/ecT21ZCKsjAA.mp4");
        map.put("第三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403f01de5285890792596910956/wuJaXbHAAa0A.mp4");
        map.put("第三章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b554ee9e5285890781246649597/K3ISfldhSgMA.mp4");
        map.put("第三章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b554f1d25285890781246649612/NBoSeVy0KjYA.mp4");
        map.put("第三章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b97dd5b5285890792596716274/PS461uqnwtoA.mp4");
        map.put("第四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af0f941e5285890792529817963/H2GrFFVnrQQA.mp4");
        map.put("第四章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cabeb185285890792529052795/bo7ZTMh1TwwA.mp4");
        map.put("第四章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba5fb28c5285890781253609702/vEqcDHQ6LMEA.mp4");
        map.put("第四章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af5638015285890816539733989/caP6iHPF1ZsA.mp4");
        map.put("第五章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403f6c5b5285890792596911169/lBBe2DQpVnQA.mp4");
        map.put("第五章-第二节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba5fb3795285890781253609778/MfTCr0qnSDYA.mp4");
        map.put("第六章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af1018645285890792529818797/YNwrygEi7k0A.mp4");
        map.put("第六章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b97e55d5285890792596716438/dWPs5QM3lEUA.mp4");
        map.put("第六章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b97e5a05285890792596716459/WvUiTdirdJoA.mp4");
        map.put("第六章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba5fb6ef5285890781253609813/uir9KvdT0KkA.mp4");
        map.put("第六章-第五节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b554f6965285890781246649751/wWUqbcHzFswA.mp4");
        map.put("第六章-第六节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b97e61f5285890792596716494/AyYSe12Wtk0A.mp4");
        map.put("第六章-第七节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b97e9555285890792596716511/qLEwqmgzb1AA.mp4");
        map.put("第七章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403f78bb5285890792596911439/vXBfnFnabsYA.mp4");
        map.put("第七章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cabf37a5285890792529052986/zMtYNGYTHUgA.mp4");
        map.put("第七章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b554fa375285890781246649806/sPq1GjrrMx0A.mp4");
        map.put("第八章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403f857e5285890792596911739/ZlGiJRhXUmIA.mp4");
        map.put("第八章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/f4510b705285890786155642334/fUiXgN3JAPsA.mp4");
        map.put("第八章-第三节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/b554fa5b5285890781246649819/17CYcMdgcbwA.mp4");
        map.put("第八章-第四节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af56b2d45285890816539734543/YKLNxwrdFakA.mp4");
        map.put("第八章-第五节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba5fbb765285890781253609937/lcNMNlHvxdAA.mp4");
        map.put("第九章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af1097db5285890792529819481/det4kmAsm18A.mp4");
        map.put("第九章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cac5d6d5285890792529053153/BuVIqevw81wA.mp4");
        map.put("第九章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b97f21e5285890792596716736/y2UIQHEKVcQA.mp4");
        map.put("第十章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403f8e425285890792596911959/DaTfCW7SQb4A.mp4");
        map.put("第十章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b97f5fb5285890792596716805/oVHbo5mrIvsA.mp4");
        map.put("第十章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cac66705285890792529053390/wISI4AfD3AoA.mp4");
        map.put("第十一章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af10a8415285890792529819838/HPX1j4qqSOQA.mp4");
        map.put("第十一章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cac6a545285890792529053466/AuTOKl47JPgA.mp4");
        map.put("第十二章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/403ffc565285890792596912217/UvIX7uUCEPoA.mp4");
        map.put("第十二章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cac6e4f5285890792529053542/rVsuXAvE3pkA.mp4");
        map.put("第十三章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/404008f15285890792596912500/jn086QVali8A.mp4");
        map.put("第十三章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b9865bf5285890792596717196/rohWlpWGRU8A.mp4");
        map.put("第十三章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b9869da5285890792596717281/au19qrpYBBEA.mp4");
        map.put("第十三章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6cc2225285890781253610106/z6tquADDwG4A.mp4");
        map.put("第十四章-第一节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/af1db8535285890792529820274/eRTi0nJ1qZYA.mp4");
        map.put("第十四章-第二节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cac7af75285890792529053838/Xi2yblEduQUA.mp4");
        map.put("第十四章-第三节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/3b9876195285890792596717541/biLVL1pLvpcA.mp4");
        map.put("第十四章-第四节", "http://1256193465.vod2.myqcloud.com/0e4f81e9vodgzp1256193465/ba6cc25f5285890781253610121/TGyj3NdhLFcA.mp4");
        map.put("第十四章-第五节", "http://1256193465.vod2.myqcloud.com/7d426eccvodcq1256193465/9cac7f575285890792529053946/YFrX3Ayi2KEA.mp4");
        System.out.println(map.size());
        downloadFile(map, filePath);
    }

    public static void downloadFile(Map<String, String> map, String filePath) {
        FileUtils.createFolder(filePath);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            fixedThreadPool.execute(() -> {
                String suffix = FileUtils.getFileSuffix(entry.getValue());
                File newFile = new File(filePath + File.separator + entry.getKey() + suffix);
                if (newFile.exists()) {
                    return;
                }
                try (InputStream inputStream = new URL(entry.getValue()).openStream();
                     FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                    // 循环取出流中的数据
                    byte[] b = new byte[10485760];
                    int len;
                    while ((len = inputStream.read(b)) > 0) {
                        fileOutputStream.write(b, 0, len);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
