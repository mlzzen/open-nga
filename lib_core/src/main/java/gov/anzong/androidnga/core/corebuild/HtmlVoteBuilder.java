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
    public static String[] getVoteScore(Map<String, String> voteMap, String voteId) {
        String[] totalVoteArr = voteMap.get("_" + voteId).split(",");
        Float max = Float.parseFloat(voteMap.get("max"));
        Float getScore = Float.parseFloat(totalVoteArr[1]) * 1000;
        Float totalScore = Float.parseFloat(totalVoteArr[0]) * max;
        return new String[]{(String.valueOf(Math.round(getScore / totalScore) / 100f)), totalVoteArr[0]};
    }

    public static Map<String, String> genVoteMap(HtmlData htmlData) {
        String[] voteData = htmlData.getVote().split("~");
        Map<String, String> voteMap = new HashMap<>();
        for (int i = 0; i < voteData.length; i += 2) {
            voteMap.put(voteData[i], voteData[i + 1]);
        }
        return voteMap;
    }

    @Override
    public CharSequence build(HtmlData htmlData) {
        if (TextUtils.isEmpty(htmlData.getVote())) {
            return "";
        }
        Map<String, String> voteMap = genVoteMap(htmlData);

        String voteType = voteMap.get("type");

        String resultHtml = "<br/><hr/>";

        if (voteType == null || voteType.equals("1")){
            resultHtml += "<div style=color:red;>本楼有投票/投注内容，在菜单中点击投票/投注按钮</div><br/>";
        }
        resultHtml += "<div>";

        for (Map.Entry<String, String> entry : voteMap.entrySet()) {
            String key = entry.getKey();
            
            if (isInteger(key)) {
                if (voteType == null || voteType.equals("1")) {
                    resultHtml = resultHtml + "<div>" + voteMap.get(key) + "&emsp;";
                    String[] voteDataValues = voteMap.get("_" + key).split(",");
                    resultHtml = resultHtml + voteDataValues[0] + "人</div>";
                } else if (voteType != null && voteType.equals("2")) {
                    resultHtml += "总分：" + getVoteScore(voteMap, key)[0] + "分<br/>共计" + getVoteScore(voteMap, key)[1] + "人评分</div>";
                } else if (voteType != null && voteType.equals("3")) {
                    resultHtml += "总分：" + voteMap.get(key) + "</div>";
                }
            }
        }
        return resultHtml;
    }

    public static boolean isInteger(String str) {
        Pattern pa = Pattern.compile("^\\d*$");
        return pa.matcher(str).matches();
    }
}
