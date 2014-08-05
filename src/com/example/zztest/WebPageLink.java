package com.example.zztest;

public class WebPageLink {
    public String title;
    public String link;

    public WebPageLink(String link, String title) {
        this.link = link;
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WebPageLink) {
            WebPageLink other = (WebPageLink) o;
            return other.link.equals(this.link);
        }

        return false;
    }

}
