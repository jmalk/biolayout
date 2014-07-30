package org.BioLayoutExpress3D.Utils;

/*
*
* User: icases
* Date: 22-oct-02
*
* @author Full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class SearchURL
{
    private String url = "";
    private String description = "";
    private String name = "";

    public SearchURL(String url)
    {
        this.url = url;
    }

    public SearchURL(String url, String name)
    {
        this.url = url;
        this.name = name;
    }

    public SearchURL(String url, String name, String description)
    {
        this.url = url;
        this.name = name;
        this.description = description;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return description;
    }


}