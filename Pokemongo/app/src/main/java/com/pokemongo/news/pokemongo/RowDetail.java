package com.pokemongo.news.pokemongo;

/**
 * Created by yako on 7/20/16 AD.
 */
public class RowDetail {

    private String id;
    private String url;
    private String title;
    private String image;
    private String site_title;
    private Integer site_js_flag;
    private String ts;

    // id
    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    // url
    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl(){
        return this.url;
    }

    // title
    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    // site_title
    public void setSiteTitle(String site_title){
        this.site_title = site_title;
    }

    public String getSiteTitle(){
        return this.site_title;
    }

    // image
    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return this.image;
    }

    // js_flag
    public void setSiteJsFlag(Integer site_js_flag){
        this.site_js_flag = site_js_flag;
    }

    public Integer getSiteJsFlag(){
        return this.site_js_flag;
    }

    // ts
    public void setTS(String ts){
        this.ts = ts;
    }

    public String getTS(){
        return this.ts;
    }
}