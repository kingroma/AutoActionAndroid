package com.cisco.epg.cjh.android.retrofit;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cisco.epg.cjh.android.activity.MainActivity;
import com.cisco.epg.cjh.android.chat.ChatControllerService;
import com.cisco.epg.cjh.android.chat.ChatObject;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/*
 * $$ : 대화창 바꿈 구분자
 * ^^ : 동일한 대화창에서 한줄 띄어쓰기 구분자
 * @@ : 동일한 대화창에서 줄 바꿈 구분자
 */

/*
무엇을 도와드릴까요?$$reply_plz$$_y고객센터_y^^고객님들이 많이 문의하는 질문이에요^^
_y1번'_y 리모컨 사용법, 고장 문의^^_y2번'_y A/S 신청, 취소^^_y3번'_y 인터넷 연결 상태

_y리모컨 설명서_y

시청연령제한 안내 입니다$$메뉴 > 설정 > 시청연령 제한 메뉴에서 확인할 수 있어요$$_y시청연령제한_y^^
자세한 내용이 궁금하다면, '영상 시청'을 선택하세요^^$$도움이 되었나요? 종료 할까요?$$reply_plz

이용해주셔서 감사합니다

보유하고 계신 휴대폰/PC용 일반 이어폰을 사용하시면 됩니다$$reply_plz

혼자 듣기 이용 시 음량은 리모컨 전면부의 음량 +/- 키로  조절해주시기 바랍니다.^^^^자체 볼륨 설정이 가능한 이어폰/헤드폰을 이용하신다면,자체 볼륨을 최대로 설정하여 이용하시기를 권유드립니다$$reply_plz$$_y고객센터_y
^^혼자 듣기 이용 안내^^^^chk_box 리모컨에 이어폰을 연결하면 연결음이 들리고, TV화면 왼쪽 상단에 [혼자 듣기]가 팝업된 후 실행^^chk_box 혼자 듣기 실행 시 TV는 자동으로 음소거 모드 작동

언제 A/S를 신청하셨나요?$$reply_plz

신청한 연락처를 말씀해 주세요$$reply_plz

맞는지 확인해주세요 (01012345678)$$reply_plz

홍길동님, 신청하신 A/S를 취소하시겠습니까?$$reply_plz

11월 9일에 신청하신 A/S 요청건은 취소 완료되었습니다^^
궁금한 사항은 고객센터로 문의해주세요^^이용해 주셔서 감사합니다

고장난 리모컨을 가지고있나요?

아무 버튼이나 눌러서 리모콘 상단에 불이 들어 오는지 지금 확인해 보세요$$reply_plz

빨간 불인가요? 노란 불인가요 ?$$reply_plz

노란불이 켜지면 배터리 교체신호 입니다. 배터리를 교체 후 사용해 보세요$$reply_plz

이용해 주셔서 감사합니다

리모콘의 전원버턴을 눌러 셋톱박스 불이 반응이 있는지 확인해 보세요$$reply_plz

페어링이 끊어졌습니다. 페어링키를 눌러 페어링을 시도해 보세요$$페어링이 되었다는 메세지가 나오면 정상으로 복구가 되었으니 사용하시면 됩니다$$이용해 주셔서 감사합니다

셋톱박스 전원 플러그를 뽑았다가 다시 꽃은 후 동작하는지 확인해 보세요^^
약1분 정도 기다려야 합니다^^
동작이 안되면 다시 한번 연락 주시기 바랍니다

TV 전원 키를 누르면 TV전원이 켜지거나 꺼지나요?^^TV가 꺼질 경우 다시 켜고 문의해 주세요$$reply_plz

설정이 안되어 있습니다^^설정 방법을 알려 드리겠습니다$$설정이 안될 경우 다음 번호로 전화 주세요^^^^ Call center : 02-000-0000

TV 수신부 앞에 방해물은 없나요?$$reply_plz

방해물을 없애고 다시 해 보세요.$$이용해 주셔서 감사합니다

다음과 같은 상황에서 일시적으로 느릴 수 있습니다$$chk_box 셋톱박스 Upgrade를 하는 경우^^chk_box 일시적인 네트워크 양이 늘어나는 경우$$10분 정도 지난 후 계속 느릴 경우 다음 번호로 연락 주시기 바랍니다. 불편을 드려 죄송합니다^^^^ Call center : 02-000-0000
 * */
public class AIApiParser {
    private static final String TAG = "AIApiParser";

    private static final String NEW_CHAT_MARK = "\\$\\$";

    private static final String NEW_LINE_MARK = "\\^\\^";

    // private static final String NEW_LINE_MARK = "@@";

    private static final String REPLY_PLZ_MARK = "reply_plz";

    private static final String YELLOW_MARK = "_y";

    private static final String CHECK_BOX__MARK = "chk_box";

    private static final String IMAGE_MARK = "img_area";

    private static final String TITLE_REGEX = "^" + YELLOW_MARK + ".*" + YELLOW_MARK + "$";

    private static final String NUMBER_TEXT_REGEX = "^" + YELLOW_MARK + ".*" + YELLOW_MARK + ".+" ;

    private StringBuilder sb = new StringBuilder();

    private ChatControllerService cc = null ;

    private String movieUrl = null ;

    public AIApiParser (ChatControllerService cc ) {
        this.cc = cc ;
    }

    public JsonObject request(String deviceId , String query){
        JsonObject o = new JsonObject();

        o.addProperty("deviceId",deviceId);
        o.addProperty("query",query);

        return o ;
    }

    private String[] splitChat(String input) {
        return input.split(NEW_CHAT_MARK);
    }


    private String[] splitNewLine(String input) {
        return input.split(NEW_LINE_MARK);
    }

    public String getAllText(){
        return sb.toString()
                .replaceAll(NEW_CHAT_MARK,". ")
                .replaceAll(NEW_LINE_MARK,". ")
                .replaceAll(REPLY_PLZ_MARK," ")
                .replaceAll(YELLOW_MARK, ". ")
                .replaceAll(IMAGE_MARK, " ")
                .replaceAll(CHECK_BOX__MARK,". ");
    }

    public List<ChatObject> parseToChatObject(AIApiResponse response){
        List<ChatObject> list = new ArrayList<ChatObject>();
        sb = new StringBuilder();
        int systemIcon = ChatObject.SYSTEM_ICON_REPLY_ID;

        if ( response != null ){
            AIApiAnswerResponse a = response.getAnswer();

            // TODO  ChatObject 만들기
            String text = a.getText();
            text = text.replaceAll("\n","");

            // TODO TEST
            if ( a.getImgUrl() != null ) {
                a.setImgUrl(a.getImgUrl().replaceAll("58.140.89.77", "10.20.0.165"));
            }
            if ( a.getMovieUrl() != null ) {
                a.setMovieUrl(a.getMovieUrl().replaceAll("58.140.89.77", "10.20.0.165"));
            }



            String[] chats = splitChat(text);

            int chatIdx = 0 ;
            for (String chat : chats) {
                String lines[] = splitNewLine(chat);
                ChatObject co = new ChatObject();

                if ( lines != null && lines.length > 0 ) {
                    for ( String line : lines ) {
                        sb.append(line);
                    }
                }

                if ( lines == null || lines.length == 0 ) {

                } else if ( lines.length == 1 && lines[0] != null && !lines[0].isEmpty() ) {
                    String line = lines[0];
                    line = line.trim();
                    // 단순 텍스트 가정
                    if ( REPLY_PLZ_MARK.equalsIgnoreCase(line) ) {
                        co.setReplyPlz(true);
                    } else if ( line.matches(TITLE_REGEX) ) {
                        co.setTitle(line.replaceAll(YELLOW_MARK,""));
                    } else {
                        co.setText(line);
                        if ( line.contains("?") ){
                            systemIcon = ChatObject.SYSTEM_ICON_CURIOUS_ID;
                        }
                    }
                } else if ( lines.length > 0 ){
                    int idx = 0 ;
                    int length = lines.length ;
                    for ( String line : lines ) {
                        line = line.trim();

                        if ( line.matches(TITLE_REGEX) ) { // _y고객센터_y
                            co.setTitle(line.replaceAll(YELLOW_MARK,""));
                        } else if ( line.matches(NUMBER_TEXT_REGEX) ) { // _y1번'_y 리모컨 사용법, 고장 문의
                            String[] split = line.replaceFirst(YELLOW_MARK,"").split(YELLOW_MARK);
                            co.addChild(cc.getSystemTextWithYellow(split[0].trim() , split[1].trim()));
                        } else if (line.startsWith(CHECK_BOX__MARK)) {
                            co.addChild(cc.getSystemTextWithCheck(line.replace(CHECK_BOX__MARK,"").trim()));
                        } else if (line.contains(IMAGE_MARK)) {

                            if ( a.getImgUrl().endsWith("001.png")){
                                // 363,204
                                co.addChild(cc.getImageView(a.getImgUrl(),363,204));
                            } else {
                                co.addChild(cc.getImageView(a.getImgUrl()));
                            }


                            if ( a.getMovieUrl() != null && !a.getMovieUrl().isEmpty() ){
                                movieUrl = a.getMovieUrl();
                                View.OnClickListener listener1 = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MainActivity.playVideo(movieUrl);
                                        // MainActivity.playVideo2(movieUrl);
                                    }
                                };

                                View.OnClickListener listener2 = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MainActivity.stopVideo();
                                        // MainActivity.stopVideo2();
                                    }
                                };

                                co.setButtons(cc.getButtonView("영상 시청","종료",listener1,listener2));
                            }
                        } else {
                            co.setText(line);
                            if ( line.contains("?") ){
                                systemIcon = ChatObject.SYSTEM_ICON_CURIOUS_ID;
                            }
                        }

                        idx ++ ;
                    }
                } else {
                    // ??
                }

                if ( chatIdx > 0 ) {
                    co.setBackground(ChatObject.SYSTEM_BACKGROUND_RADIUS_2);
                }

                list.add(co);
                chatIdx++;
            }



            if (list.size() > 0 ) {
                list.get(0).setSystemIconId(systemIcon);
            }
        }

        return list ;
    }
}
