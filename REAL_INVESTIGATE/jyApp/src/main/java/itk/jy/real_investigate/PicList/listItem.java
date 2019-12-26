package itk.jy.real_investigate.PicList;

public class listItem {
    private String addr;
    private String pic;

    public listItem(String ad, String pi) {
        this.addr = ad;
        this.pic = pi;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String ad) {
        this.addr = ad;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pi) {
        this.pic = pi;
    }


}
