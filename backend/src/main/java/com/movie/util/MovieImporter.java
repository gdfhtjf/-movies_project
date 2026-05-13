package com.movie.util;

import com.movie.entity.Movie;
import com.movie.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 电影数据导入工具类
 * 可通过启动参数控制是否导入
 */
@Component
public class MovieImporter implements CommandLineRunner {

    @Autowired
    private MovieMapper movieMapper;

    // 设置为false时不导入
    private static final boolean SHOULD_IMPORT = false;

    @Override
    public void run(String... args) {
        if (!SHOULD_IMPORT) {
            System.out.println("[MovieImporter] 跳过导入电影数据");
            return;
        }

        System.out.println("[MovieImporter] 开始导入电影数据...");

        try {
            List<Movie> movies = createMovies();
            int imported = 0;
            int skipped = 0;
            for (Movie movie : movies) {
                if (movieMapper.selectCount(com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery(Movie.class).eq(Movie::getTitle, movie.getTitle())) > 0) {
                    System.out.println("[MovieImporter] 跳过已存在: " + movie.getTitle());
                    skipped++;
                } else {
                    movieMapper.insert(movie);
                    System.out.println("[MovieImporter] 已导入: " + movie.getTitle());
                    imported++;
                }
            }

            System.out.println("[MovieImporter] 导入完成！新增: " + imported + " 部，跳过已存在: " + skipped + " 部");
        } catch (Exception e) {
            System.err.println("[MovieImporter] 导入失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Movie> createMovies() {
        List<Movie> movies = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 1. 流浪地球2
        movies.add(createMovie(
                "流浪地球2", "郭帆", new BigDecimal("49.90"), 100,
                "liu-lang-di-qiu-2.jpg", "download/trailer_liulang2.mp4",
                "太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。然而宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出。",
                "吴京,李雪健,沙溢,宁理,王智", "2023-01-22", 173, "科幻,冒险", now
        ));

        // 2. 满江红
        movies.add(createMovie(
                "满江红", "张艺谋", new BigDecimal("45.00"), 80,
                "man-jiang-hong.jpg", "download/trailer_manjianghong.mp4",
                "南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈。会谈前夜，金国使者死在宰相驻地，所携密信不翼而飞。",
                "沈腾,易烊千玺,张译,雷佳音,岳云鹏", "2023-01-22", 159, "悬疑,喜剧", now
        ));

        // 3. 长津湖
        movies.add(createMovie(
                "长津湖", "陈凯歌,徐克,林超贤", new BigDecimal("42.00"), 90,
                "chang-jin-hu.jpg", "download/trailer_changjinhu.mp4",
                "1950年，中国人民志愿军赴朝作战，在极寒严酷环境下，东线作战部队凭着钢铁意志和英勇无畏的战斗精神一路追击，奋勇杀敌。",
                "吴京,易烊千玺,段奕宏,朱亚文,李晨", "2021-09-30", 176, "战争,历史", now
        ));

        // 4. 你好，李焕英
        movies.add(createMovie(
                "你好，李焕英", "贾玲", new BigDecimal("38.00"), 75,
                "ni-hao-li-huan-ying.jpg", "download/trailer_lihuanying.mp4",
                "2001年的某一天，刚刚考上大学的贾晓玲经历了人生中的一次大起大落。一心想要成为母亲骄傲的她却因母亲突遭严重意外而悲痛万分。",
                "贾玲,张小斐,沈腾,陈赫,刘佳", "2021-02-12", 128, "喜剧,剧情", now
        ));

        // 5. 唐人街探案3
        movies.add(createMovie(
                "唐人街探案3", "陈思诚", new BigDecimal("40.00"), 85,
                "tang-ren-jie-tan-an-3.jpg", "download/trailer_tangren3.mp4",
                "继曼谷、纽约之后，东京再出大案。唐人街神探唐仁、秦风受侦探野田昊的邀请前往破案。",
                "王宝强,刘昊然,妻夫木聪,托尼·贾,长泽雅美", "2021-02-12", 136, "喜剧,悬疑", now
        ));

        // 6. 我不是药神
        movies.add(createMovie(
                "我不是药神", "文牧野", new BigDecimal("35.00"), 95,
                "wo-bu-shi-yao-shen.jpg", "download/trailer_yaoshen.mp4",
                "一位不速之客的意外到访，打破了神油店老板程勇的平凡人生，他从一个交不起房租的男性保健品商贩，一跃成为印度仿制药“格列宁”的独家代理商。",
                "徐峥,王传君,周一围,谭卓,章宇", "2018-07-05", 117, "剧情,喜剧", now
        ));

        // 7. 哪吒之魔童降世
        movies.add(createMovie(
                "哪吒之魔童降世", "饺子", new BigDecimal("39.00"), 100,
                "ne-zha.jpg", "download/trailer_nezha.mp4",
                "天地灵气孕育出一颗能量巨大的混元珠，元始天尊将混元珠提炼成灵珠和魔丸，灵珠投胎为人，助周伐纣时可堪大用；而魔丸则会诞出魔王，为祸人间。",
                "吕艳婷,囧森瑟夫,瀚墨,陈浩,绿绮", "2019-07-26", 110, "动画,奇幻", now
        ));

        // 8. 流浪地球
        movies.add(createMovie(
                "流浪地球", "郭帆", new BigDecimal("38.00"), 88,
                "liu-lang-di-qiu.jpg", "download/trailer_liulang.mp4",
                "太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。",
                "吴京,屈楚萧,李光洁,吴孟达,赵今麦", "2019-02-05", 125, "科幻,冒险", now
        ));

        // 9. 长津湖之水门桥
        movies.add(createMovie(
                "长津湖之水门桥", "徐克,陈凯歌,林超贤", new BigDecimal("48.00"), 85,
                "shui-men-qiao.jpg", "download/trailer_shuimenqiao.mp4",
                "以抗美援朝战争第二次战役中的长津湖战役为背景，讲述七连战士们在结束了新兴里和下碣隅里的战斗之后，又接到了更艰巨的任务。",
                "吴京,易烊千玺,朱亚文,李晨,段奕宏", "2022-02-01", 149, "战争,历史", now
        ));

        // 10. 独行月球
        movies.add(createMovie(
                "独行月球", "张吃鱼", new BigDecimal("42.00"), 90,
                "du-xing-yue-qiu.jpg", "download/trailer_duxingyueqiu.mp4",
                "人类为抵御小行星的撞击，拯救地球，在月球部署了月盾计划。陨石提前来袭，全员紧急撤离时，维修工独孤月因为意外，错过了领队马蓝星的撤离通知，一个人落在了月球。",
                "沈腾,马丽,常远,李诚儒,黄才伦", "2022-07-29", 122, "喜剧,科幻", now
        ));

        // 11. 这个杀手不太冷静
        movies.add(createMovie(
                "这个杀手不太冷静", "邢文雄", new BigDecimal("39.00"), 80,
                "sha-shou-bu-tai-leng-jing.jpg", "download/trailer_shashou.mp4",
                "魏成功非常热爱表演，然而只是个跑龙套的小演员。一个偶然的机会，他被女演员米兰邀请假扮“杀手卡尔”，从此开启了笑中带泪的经历。",
                "马丽,魏翔,陈明昊,周大勇,黄才伦", "2022-02-01", 109, "喜剧,剧情", now
        ));

        // 12. 万里归途
        movies.add(createMovie(
                "万里归途", "饶晓志", new BigDecimal("45.00"), 85,
                "wan-li-gui-tu.jpg", "download/trailer_wanliguitu.mp4",
                "努米亚共和国爆发战乱，前驻地外交官宗大伟与外交部新人成朗受命前往协助撤侨。",
                "张译,王俊凯,殷桃,成泰燊,张子贤", "2022-09-30", 137, "战争,剧情", now
        ));

        // 13. 消失的她
        movies.add(createMovie(
                "消失的她", "崔睿,刘翔", new BigDecimal("44.00"), 90,
                "xiao-shi-de-ta.jpg", "download/trailer_xiaoshideta.mp4",
                "何非的妻子李木子在结婚周年旅行中离奇消失，在何非苦寻无果之时，妻子再次现身，何非却坚持眼前的陌生女人并非妻子。",
                "朱一龙,倪妮,文咏珊,杜江,黄子琪", "2023-06-22", 122, "悬疑,剧情", now
        ));

        // 14. 孤注一掷
        movies.add(createMovie(
                "孤注一掷", "申奥", new BigDecimal("46.00"), 88,
                "gu-zhu-yi-zhi.jpg", "download/trailer_guzhuyizhi.mp4",
                "诈骗工厂主管陆秉坤招揽潘生等人出国，他们实则是被拐卖到诈骗工厂进行网络诈骗的受害者。",
                "张艺兴,金晨,咏梅,王传君,王大陆", "2023-08-08", 130, "犯罪,剧情", now
        ));

        // 15. 封神第一部
        movies.add(createMovie(
                "封神第一部", "乌尔善", new BigDecimal("40.00"), 92,
                "feng-shen-di-yi-bu.jpg", "download/trailer_fengshen.mp4",
                "商王殷寿与狐妖妲己勾结，暴虐无道，引发天谴。姜子牙、姬发、雷震子等仁人志士联手，共同对抗邪恶势力。",
                "费翔,李雪健,黄渤,于适,陈牧驰", "2023-07-20", 148, "奇幻,动作", now
        ));

        return movies;
    }

    private Movie createMovie(String title, String director, BigDecimal price,
                              Integer stock, String posterPath, String trailerPath,
                              String description, String cast, String releaseDateStr,
                              Integer duration, String genre, LocalDateTime now) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDirector(director);
        movie.setPrice(price);
        movie.setStock(stock);
        movie.setPosterPath(posterPath);
        movie.setTrailerPath(trailerPath);
        movie.setDescription(description);
        movie.setCast(cast);
        movie.setReleaseDate(LocalDate.parse(releaseDateStr, DateTimeFormatter.ISO_LOCAL_DATE));
        movie.setDuration(duration);
        movie.setGenre(genre);
        movie.setCreateTime(now);
        movie.setUpdateTime(now);
        return movie;
    }
}
