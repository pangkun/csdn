import controller.Dao;
import javabean.Article;
import javabean.Author;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

public class MySpider implements PageProcessor {

    Site site = Site.me().setSleepTime(1000).setRetryTimes(3).setTimeOut(300);

    public void process(Page page) {
        //目录页
        if (isTitleList(page.getUrl().get())) {
            List<String> all = page.getHtml().xpath("//*[@id=\"article_list\"]//h1/span/a/@href").all();
            if (all == null || all.size() <= 0) {
                all = page.getHtml().xpath("//*[@id=\"main\"]//ul[2]/li/a/@href").all();
            }
            if (all != null && all.size() > 0) {
                String url = page.getUrl().get();
                String nextPage = url.split("/list/")[0] + "/list/" + (Integer.valueOf(url.split("/list/")[1]) + 1);
                all.add(nextPage);
                page.addTargetRequests(all);
            } else {
                String nextAuthor = nextAuthor(getNameFromUrl(page.getUrl().get()));
                page.addTargetRequest(nextAuthor + "/article/list/1");
            }
        } else {
            //文章页
//            System.out.println(page.getUrl().get());
            saveArticle(page);
            List<String> all = page.getHtml().links().all();//可以改
            System.out.println("还有不少呢" + all.size());
            for (String s : all) {
                System.out.println(s+"---------all");
                if (s.matches("https://blog\\.csdn\\.net/\\w+/article/details/\\d+")) {
//                    saveAuthor(page);
                    System.out.println(s+"-----yes");
                }
            }
        }
    }


    public static void main(String[] args) {
        Spider.create(new MySpider()).addUrl("https://blog.csdn.net/china_demon/article/list/1").thread(5).run();
    }

    private String nextAuthor(String authorName) {
        System.out.println("before next author");
        Author author = Dao.findNextAuthor(authorName);
        System.out.println("next author:" + author.getUrl());
        return author.getUrl();
    }

    private void saveArticle(Page page) {
        Html html = page.getHtml();
        String title = html.xpath("//h1/text()").get();
        String date = html.xpath("//span[@class='time']/text()").get();
        String author = html.xpath("//*[@id=\"uid\"]/text()").get();
        String time = html.xpath("//button/span/text()").get();
        if (time == null || date == null) {
            title = html.xpath("//*[@id=\"article_details\"]/div[1]/h1/span/a/text()").get();
            date = html.xpath("//*[@id=\"article_details\"]/div[2]/div[2]/span[1]/text()").get();
            author = html.xpath("//*[@id=\"uid\"]/text()").get();
            time = html.xpath("//*[@id=\"article_details\"]/div[2]/div[2]/span[2]/text()").get();
        }
        Article article = new Article();
        article.setAuthor(author);
        article.setDate(date);
        article.setTime(time);
        article.setTitle(title);
        article.setUrl(page.getUrl().get());
        Dao.saveArticle(article);
    }

    private void saveAuthor(Page page) {
        Author author = new Author();
        author.setUrl(page.getUrl().get().split("/article/")[0]);
        System.out.println("saveAuthor-"+author.getUrl());
        if (Dao.findAuthor(author.getUrl()) == null)
            Dao.saveAuthor(author);
    }

    public String getNameFromUrl(String url) {
        String authorName = url.split("blog.csdn.net/")[1].split("/article/")[0];
        return authorName;
    }

    private boolean isTitleList(String s) {
        return s.matches("https://blog\\.csdn\\.net/\\w+/article/list/\\d+");
    }

    public Site getSite() {
        return site;
    }
}
