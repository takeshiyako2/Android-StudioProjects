package headerbutton.post.nine.getjsontolist;

public class RowDetail {


    private String id;
    private String url;
    private String title;
    private String image;
    private String youtube_id;
    private Integer youtube_flag;

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

    // image
    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return this.image;
    }

    // youtube_id
    public void setYouTubeID(String youtube_id){
        this.youtube_id = youtube_id;
    }

    public String getYouTubeID(){
        return this.youtube_id;
    }

    // youtube_flag
    public void setYouTubeFlag(Integer youtube_flag){
        this.youtube_flag = youtube_flag;
    }

    public Integer getYouTubeFlag(){
        return this.youtube_flag;
    }
}
