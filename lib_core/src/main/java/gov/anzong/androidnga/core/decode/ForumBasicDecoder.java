package gov.anzong.androidnga.core.decode;

import android.icu.number.CompactNotation;
import android.text.TextUtils;

import java.util.Map;

import gov.anzong.androidnga.base.util.StringUtils;
import gov.anzong.androidnga.core.corebuild.HtmlVoteBuilder;
import gov.anzong.androidnga.core.data.HtmlData;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumBasicDecoder implements IForumDecoder {

    private static final String lesserNukeStyle = "<div style='border:1px solid #B63F32;margin:10px 10px 10px 10px;padding:10px' > <span style='color:#EE8A9E'>用户因此贴被暂时禁言，此效果不会累加</span><br/>";
    private static final String styleAlignRight = "<div style='text-align:right' >";
    private static final String styleAlignLeft = "<div style='text-align:left' >";
    private static final String styleAlignCenter = "<div style='text-align:center' >";
    private static final String styleColor = "<span style='color:$1' >";
    private static final String endDiv = "</div>";

    private static final String STYLE_QUOTE = "<div class='quote' >";

    @Override
    public String decode(String content, HtmlData htmlData) {
        Boolean isVote = false;
        if (!TextUtils.isEmpty(htmlData.getVote())) {
            isVote = true;
        }

        if (StringUtils.isEmpty(content)) {
            return "";
        }
        // s = StringUtils.unEscapeHtml(s);

        String quoteStyle = STYLE_QUOTE;

        final String styleLeft = "<div style='float:left' >";
        final String styleRight = "<div style='float:right' >";
        content = StringUtils.replaceAll(content, ignoreCaseTag + "&amp;", "&");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[l\\]", styleLeft);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/l\\]", endDiv);
        // content = StringUtils.replaceAll(content, "\\[L\\]", styleLeft);
        // content = StringUtils.replaceAll(content, "\\[/L\\]", endDiv);

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[r\\]", styleRight);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/r\\]", endDiv);
        // content = StringUtils.replaceAll(content, "\\[R\\]", styleRight);
        // content = StringUtils.replaceAll(content, "\\[/R\\]", endDiv);

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[align=right\\]", styleAlignRight);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[align=left\\]", styleAlignLeft);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[align=center\\]", styleAlignCenter);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/align\\]", endDiv);

        content = StringUtils.replaceAll(content,
                ignoreCaseTag
                        + "\\[b\\]Reply to \\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\] (.+?)\\[/b\\]",
                "[quote]Reply to [b]<a href='" + htmlData.getNGAHost() + "read.php?searchpost=1&pid=$1&tid=$2' style='font-weight: bold;color:#3181f4'>[Reply]</a> $4[/b][/quote]");

        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?searchpost=1&pid=$1&tid=$2' style='font-weight: bold;color:#3181f4'>[Reply]</a>");

        // 某些帖子会导致这个方法卡住, 暂时不清楚原因, 和这个方法的作用.... by elrond
        /*content = StringUtils.replaceAll(content, 
                ignoreCaseTag + "={3,}((^=){0,}(.*?){0,}(^=){0,})={3,}",
                "<h4 style='font-weight: bold;border-bottom: 1px solid #AAA;clear: both;margin-bottom: 0px;'>$1</h4>");*/

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[quote\\]", quoteStyle);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/quote\\]", endDiv);

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[code\\]", quoteStyle + "Code:");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[code(.+?)\\]", quoteStyle);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/code\\]", endDiv);
        // reply
        // content = StringUtils.replaceAll(content, 
        // ignoreCaseTag +"\\[pid=\\d+\\]Reply\\[/pid\\]", "Reply");
        // content = StringUtils.replaceAll(content, 
        // ignoreCaseTag +"\\[pid=\\d+,\\d+,\\d\\]Reply\\[/pid\\]", "Reply");

        // topic
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[tid=\\d+\\]Topic\\[/pid\\]",
                "Topic");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[tid=?(\\d{0,50})\\]Topic\\[/tid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;color:#3181f4'>[Topic]</a>");
        // reply
        // s =
        // s.replaceAll("\\[b\\]Reply to \\[pid=\\d+\\]Reply\\[/pid\\] (Post by .+ \\(\\d{4,4}-\\d\\d-\\d\\d \\d\\d:\\d\\d\\))\\[/b\\]"
        // , "Reply to Reply <b>$1</b>");
        // 转换 tag
        // [b]
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[b\\]", "<b>");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/b\\]", "</b>"/* "</font>" */);

        // item
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[item\\]", "<b>");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/item\\]", "</b>");

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[u\\]", "<u>");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/u\\]", "</u>");

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[s:(\\d+)\\]",
                "<img src='file:///android_asset/a$1.gif'>");
        content = content.replace(ignoreCaseTag + "<br/><br/>", "<br/>");
        // [url][/url]
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[url\\]/([^\\[|\\]]+)\\[/url\\]",
                "<a href=\"" + htmlData.getNGAHost() + "$1\" style='color:#3181f4'>" + htmlData.getNGAHost() + "$1</a>");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[url\\]([^\\[|\\]]+)\\[/url\\]",
                "<a href=\"$1\" style='color:#3181f4'>$1</a>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[url=/([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]",
                "<a href=\"" + htmlData.getNGAHost() + "$1\" style='color:#3181f4'>$2</a>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[url=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]",
                "<a href=\"$1\">$2</a>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                + "\\[uid=?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "Post by\\s{0,}([^\\[\\s]{1,})\\s{0,}\\(",
                "Post by <a href='" + htmlData.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;color:#3181f4'>[$1]</a> (");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[@(.{2,20}?)\\]",
                "<a href='" + htmlData.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;color:#3181f4'>[@$1]</a>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                + "\\[uid=-?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[hip\\](.+?)\\[\\/hip\\]",
                "$1");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[tid=?(\\d{0,50})\\](.+?)\\[/tid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;color:#3181f4'>[$2]</a>");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag
                        + "\\[pid=(.+?)\\]\\[/pid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;color:#3181f4'>[Reply]</a>");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag
                        + "\\[pid=(.+?)\\](.+?)\\[/pid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;color:#3181f4'>[$2]</a>");
        // flash
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[flash\\](http[^\\[|\\]]+)\\[/flash\\]",
                "<a href=\"$1\"><img src='file:///android_asset/flash.png' style= 'max-width:100%;' ></a>");
        // color

        // content = StringUtils.replaceAll(content, "\\[color=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/color\\]"
        // ,"<b style=\"color:$1\">$2</b>");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[color=([^\\[|\\]]+)\\]",
                styleColor);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/color\\]", "</span>");

        // lessernuke
        content = StringUtils.replaceAll(content, "\\[lessernuke\\]", lesserNukeStyle);
        content = StringUtils.replaceAll(content, "\\[/lessernuke\\]", endDiv);

        // [table][/table]
        content = StringUtils.replaceAll(content, "\\[table](.*?)\\[/table]", "<div><table cellspacing='0px' class='default'><tbody>$1</tbody></table></div>");

        // [tr][/tr]
        content = StringUtils.replaceAll(content, "\\[tr](.*?)\\[/tr]", "<tr>$1</tr>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td[ ]*(\\d+)\\]",
                "<td style='border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' rowspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td colspan='$1' rowspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' colspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$1' colspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$3' colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$2' colspan='$3'  style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");


        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\scolspan=?(\\d+)\\]",
                "<td colspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\srowspan=?(\\d+)\\]",
                "<td rowspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        content = StringUtils.replaceAll(content, "\\[td\\]", "<td style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        content = StringUtils.replaceAll(content, "\\[/td\\]", "</td>");
        // 处理表格外面的额外空行
        content = StringUtils.replaceAll(content, "<([/]?(table|tbody|tr|td))><br/>", "<$1>");
        // [i][/i]
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[i\\]",
                "<i style=\"font-style:italic\">");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/i\\]", "</i>");
        // [del][/del]
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[del\\]", "<del class=\"gray\">");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/del\\]", "</del>");

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[font=([^\\[|\\]]+)\\]",
                "<span style=\"font-family:$1\">");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/font\\]", "</span>");

        // size
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[size=(\\d+)%?\\]",
                "<span style=\"font-size:$1%;line-height:$1%\">");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/size\\]", "</span>");


        // [list][/list]
        // TODO: 2018/9/18  部分页面里和 collapse 标签有冲突 http://bbs.nga.cn/read.php?tid=14949699
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[list\\](.+?)\\[/list\\]", "<ul>$1</ul>");
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[list\\]", "");
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[/list\\]", "");
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[\\*\\](.+?)<br/>", "<li>$1</li>");

        // [h][/h]
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[h](.+?)\\[/h]", "<b>$1</b>");

        // [collapse][/collapse]
        content = StringUtils.replaceAll(content, "\\[collapse=(.*?)](.*?)\\[/collapse]", "<div><button onclick='toggleCollapse(this,\"$1\")'>点击显示内容 : $1</button><div name='collapse' class='collapse' style='display:none'>$2</div></div>");
        content = StringUtils.replaceAll(content, "\\[collapse](.*?)\\[/collapse]", "<div><button onclick='toggleCollapse(this)'>点击显示内容</button><div name='collapse' class='collapse'style='display:none' >$1</div></div>");

        // [flash=video]/flash]
        content = StringUtils.replaceAll(content, "\\[flash=video].(.*?)\\[/flash]", "<video src='http://img.ngacn.cc/attachments$1' controls='controls'></video>");

        // [flash=audio][/flash]"
        content = StringUtils.replaceAll(content, "\\[flash=audio].(.*?)\\[/flash]", "<audio src='http://img.ngacn.cc/attachments$1&filename=nga_audio.mp3' controls='controls'></audio>");

        // 游戏评分
        // [randomblock]<br/>[fixsize height 52 width 50 90]
        content = StringUtils.replaceAll(content, "\\[randomblock]\\<br/>\\[fixsize height 52 width 50 90]", "<div class=\"fixblk\" style=\" clear: both; overflow: hidden; width: 100%;height:700px; box-shadow: rgb(0, 0, 0) 0px 0px 15px -8px inset; background: rgb(245, 232, 203); \"><br/><div style=\"margin: auto; overflow: hidden; position: relative; z-index: 0;height:52em; max-width: 90em; min-width: 28em; transform-origin: left top; transform: scale(0.496402, 0.496402);\">");
        content = StringUtils.replaceAll(content, "\\[/randomblock]", "</div>");

        // [style float left margin 1 0 1 1 width 9 height 7 background #b22222 align center border-radius 0.3 font 0 #fff]
        content = StringUtils.replaceAll(content, "\\[style float left margin 1 0 1 1 width 9 height 7 background #b22222 align center border-radius 0.3 font 0 #fff]", "<div style=\"display:inline-block;float:left;margin:1em 0em 1em 1em;width:9em;height:7em;background:#b22222;text-align:center;border-radius:0.3em;color:#fff;\">");

        // [style font 4 line-height 1.3 innerHTML &#36;votedata_voteavgvalue]
        content = StringUtils.replaceAll(content, "\\[style font 4 line-height 1.3 innerHTML &#36;votedata_voteavgvalue]", "<div style=\"display:inline-block;font-size:4em;line-height:1.3em;\">&#36;votedata_voteavgvalue");
        // [style font 1 line-height 1.2]
        content = StringUtils.replaceAll(content, "\\[style font 1 line-height 1.2]", "<div style=\"display:inline-block;font-size:1em;line-height:1.2em;\">");

        // [style innerHTML &#36;votedata_usernum]
        content = StringUtils.replaceAll(content, "\\[style innerHTML &#36;votedata_usernum]", "<div style=\"display:inline-block;\">&#36;votedata_usernum");

        // [style float left margin 1 0 1 1 color #444]
        content = StringUtils.replaceAll(content, "\\[style float left margin 1 0 1 1 color #444]", "<div style=\"display:inline-block;float:left;margin:1em 0em 1em 1em;color:#444;\">");

        // [style align justify-all]
        content = StringUtils.replaceAll(content, "\\[style align justify-all]", "<div style=\"display:inline-block;text-align:justify;text-align-last:justify;text-justify:inter-word;\">");

        // [style font 3 #444 line-height 1 width 100%]
        content = StringUtils.replaceAll(content, "\\[style font 3 #444 line-height 1 width 100%]", "<div style=\"display:inline-block;font-size:3em;color:#444;line-height:1em;width:100%;\">");

        content = StringUtils.replaceAll(content, "\\[style width 100% line-height 2.5]", "<div style=\"display:inline-block;width:100%;line-height:2.5em;\">");

        content = StringUtils.replaceAll(content, "\\[style line-height 1.5]", "<div style=\"display:inline-block;line-height:1.5em;\">");

        content = StringUtils.replaceAll(content, "\\[style color #fff padding 0 0.5 background #0c7da8 border-radius 0.2]", "<div style=\"display:inline-block;color:#fff;padding:0em 0.5em;background:#0c7da8;border-radius:0.2em;\">");

        content = StringUtils.replaceAll(content, "\\[style color #444 margin 0 1 1 1 float left clear both]", "<div style=\"display:inline-block;color:#444;margin:0em 1em 1em 1em;float:left;clear:both;\">");

        content = StringUtils.replaceAll(content, "\\[comment game_title_image]\\[style border-radius 0.3 width 50 src .", "<img src=\"https://img.nga.178.com/attachments");

        content = StringUtils.replaceAll(content, "]\\[/style]\\[/comment game_title_image]", "\" style=\"display:inline-block;border-radius:0.3em;width:50em;\">");

        content = StringUtils.replaceAll(content, "\\[comment game_title_cn]", "");
        content = StringUtils.replaceAll(content, "\\[/comment game_title_cn]", "");

        content = StringUtils.replaceAll(content, "\\[comment game_title]", "");
        content = StringUtils.replaceAll(content, "\\[/comment game_title]", "");

        content = StringUtils.replaceAll(content, "\\[style float left width 20]", "<div style=\"display:inline-block;float:left;width:20em;\">");

        content = StringUtils.replaceAll(content, "\\[style float left]", "<div style=\"display:inline-block;float:left;\">");
        content = StringUtils.replaceAll(content, "\\[style font 2 line-height 1.5]", "<div style=\"display:inline-block;font-size:2em;line-height:1.5em;\">");

        content = StringUtils.replaceAll(content, "\\[comment game_release]", "");
        content = StringUtils.replaceAll(content, "\\[/comment game_release]", "");

        content = StringUtils.replaceAll(content, "\\[style float left clear both]", "<div style=\"display:inline-block;float:left;clear:both;\">");

        content = StringUtils.replaceAll(content, "\\[comment game_website]", "");
        content = StringUtils.replaceAll(content, "\\[/comment game_website]", "");

        content = StringUtils.replaceAll(content, "\\[style font 2 #b22222 line-height 1.5]", "<div style=\"display:inline-block;font-size:2em;color:#b22222;line-height:1.5em;\">");

        if (isVote) {
            Map<String, String> voteMap = HtmlVoteBuilder.genVoteMap(htmlData);
            for (Map.Entry<String, String> entry : voteMap.entrySet()) {
                String key = entry.getKey();
                if (HtmlVoteBuilder.isInteger(key) && voteMap.get("type").equals("2")) {
                    String[] voteInfo = HtmlVoteBuilder.getVoteScore(voteMap, key);
                    content = StringUtils.replaceAll(content, "&#36;votedata_voteavgvalue", voteInfo[0]);
                    content = StringUtils.replaceAll(content, "&#36;votedata_usernum", voteInfo[1]);
                }
            }
        }


        content = StringUtils.replaceAll(content, "\\[symbol link]", "");
        content = StringUtils.replaceAll(content, "\\[stripbr]", "");
        content = StringUtils.replaceAll(content, "\\[comment game_type]", "");
        content = StringUtils.replaceAll(content, "\\[/comment game_type]", "");
        content = StringUtils.replaceAll(content, "\\[comment game_publisher]", "");
        content = StringUtils.replaceAll(content, "\\[/comment game_publisher]", "");
        content = StringUtils.replaceAll(content, "\\[comment game_devloper]", "");
        content = StringUtils.replaceAll(content, "\\[/comment game_devloper]", "");
        content = StringUtils.replaceAll(content, "\\[/style]\\<br/>\\<br/>\\<br/>", "</div><br/>");
        content = StringUtils.replaceAll(content, "\\[/style]\\<br/>\\<br/>", "</div><br/>");
        // [/style]
        content = StringUtils.replaceAll(content, "\\[/style]", "</div>");
        return content;
    }
}
