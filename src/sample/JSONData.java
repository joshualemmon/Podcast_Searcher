package sample;

import org.json.simple.JSONObject;

/**
 * Created by josh on 16/04/16.
 */
public class JSONData
{
    private String trackName;
    private String artistName;
    private String rating;
    private String country;
    private String date;

    //out of time but would set each member to respective data using jo.get(key)
    //once that is done table should be able to update
    public JSONData(JSONObject jo)
    {
        this.trackName = (String)jo.get("trackName");
        this.artistName = (String)jo.get("artistName");
        this.rating = (String)jo.get("contentAdvisoryRating");
        this.country = (String)jo.get("country");
        this.date = (String)jo.get("releaseDate");

    }

    public String getTrackName()
    {
        return this.trackName;
    }

    public String getArtistName()
    {
        return this.artistName;
    }
    public String getRating()
    {
        return this.rating;
    }
    public String getCountry()
    {
        return this.country;
    }
    public String getDate()
    {
        return this.date;
    }
}
