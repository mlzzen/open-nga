package gov.anzong.androidnga.core.corebuild;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import gov.anzong.androidnga.core.data.HtmlData;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlVoteBuilder implements IHtmlBuild {

    @Override
    public CharSequence build(HtmlData htmlData) {
        if (TextUtils.isEmpty(htmlData.getVote())) {
            return "";
        }
        String[] voteData = htmlData.getVote().split("~");
        Map<String, String> voteMap = new HashMap<>();
        for (int i = 0; i < voteData.length; i += 2) {
            voteMap.put(voteData[i], voteData[i + 1]);
        }

        String resultHtml = "<br/><hr/><div>";

        for (Map.Entry<String, String> entry : voteMap.entrySet()) {
            String key = entry.getKey();
            if (isInteger(key)) {
                if (voteMap.get("type").equals("1")) {
                    resultHtml = resultHtml + "<div>" + voteMap.get(key) + "&emsp;";
                    String[] voteDataValues = voteMap.get("_" + key).split(",");
                    resultHtml = resultHtml + voteDataValues[0] + "人</div>";
                } else if (voteMap.get("type").equals("2")) {
                    String[] totalVoteArr = voteMap.get("_" + key).split(",");
                    Float max = Float.parseFloat(voteMap.get("max"));
                    Float getScore = Float.parseFloat(totalVoteArr[1]) * 10000;
                    Float totalScore = Float.parseFloat(totalVoteArr[0]) * max;
                    resultHtml += "总分：" + Math.round(getScore / totalScore) / 1000f + "分<br/>共计" + totalVoteArr[0] + "人评分</div>";
                } else if (voteMap.get("type").equals("3")) {
                    resultHtml += "总分：" + voteMap.get(key) + "</div>";
                }
            }
        }
        return resultHtml;
    }

    private boolean isInteger(String str) {
        Pattern pa = Pattern.compile("^\\d*$");
        return pa.matcher(str).matches();
    }
}
