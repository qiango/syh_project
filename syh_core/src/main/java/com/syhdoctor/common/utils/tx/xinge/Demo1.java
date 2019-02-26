//package com.syhdoctor.common.utils.tx.xinge;
//
//
//public class Demo1 {
//
//
//    public static void main(String[] args) {
//
//
//        //PushAppUtil.pushAndroid(2100312083L,"9550bc0af1ee65a1b2c211ac8b0d9b02","s","医生端推送","12","adba841d400bf7a14502a454e0ea32cf5f62288e");
////        appId = 2200312287L;
////        secretKey = "ef0e72f5c37f6208691abaaefc07b7cd";
//        //PushAppUtil.pushAndroid(2100311976L,"cdfb6146d029639faa609fea8380f054","s","用户端推送","12","adba841d400bf7a14502a454e0ea32cf5f62288e");
//     PushAppUtil.pushIos(2200312287L, "ef0e72f5c37f6208691abaaefc07b7cd", "测试", "错误", 4, "256", "8c5006e67a5d5ee1ba1511bde3c89d650899446858ed3780bfb0d6d2868c4343");
////
////        XingeApp xinge = new XingeApp(2100312083, "9550bc0af1ee65a1b2c211ac8b0d9b02");
////
////
//////        long price = 15;
//////        long result = price / 7;
//////        System.out.println(result);
////        Message message = new Message();
////        message.setType(1);
////        ClickAction action = new ClickAction();
////        action.setActionType(3);
////        action.setIntent("syhscheme://com.syhdoctor.user/department_detail?typename=1");
////        message.setTitle("神鼎飞丹1111砂发送到发送到");
////        message.setContent("说的都1312323是范德萨发生");
////        message.setAction(action);
////
////
////        JSONObject jsonObject = xinge.pushSingleDevice("0cfe04391ac5b7b7420b9f0c5b881f5a88ded88b", message);
////        System.out.println(jsonObject.toString());
//
//        //XingeApp.pushTokenAndroid(2100312083, "secretKey", "test", "测试", "token")
//
//        //JSONObject ret = xinge.pushSingleDevice("token", message);
//
//
////        XingeApp xingeApp = new XingeApp("1c5ef93a51bca", "cdfb6146d029639faa609fea8380f054");
////
////
////        Map<String, Object> customMp = new HashMap<>();
////        customMp.put("type", 1);
////        customMp.put("typename", 1);
////
////        String customStr = JsonUtil.getInstance().toJson(customMp);
////
////        Message message = new Message();
////
////        ClickAction clickAction = new ClickAction();
////        clickAction.setActionType(ClickAction.TYPE_INTENT);
////        //clickAction.setActivity("syhscheme://com.syhdoctor.push/department_detail");
////        clickAction.setIntent("syhscheme://com.syhdoctor.push/department_detail");
////
////        MessageAndroid messageAndroid = new MessageAndroid();
////        messageAndroid.setCustom_content(customStr);
////        messageAndroid.setAction(clickAction);
////
////        message.setTitle("测试测试测试");
////        message.setContent("内容内容内容");
////        message.setAndroid(messageAndroid);
////
////        Map<String, Object> paramsMp = new HashMap<>();
////        paramsMp.put("audience_type", "token");
////        paramsMp.put("platform", Platform.android);
////        paramsMp.put("message", message);
////        paramsMp.put("message_type", MessageType.notify);
////        paramsMp.put("token_list", Arrays.asList("4619f716c4ac54144a72a052fd326ce99133aa37"));
////        paramsMp.put("multi_pkg", true);
////
////        String params = JsonUtil.getInstance().gsonToJson(paramsMp);
////        try {
////            System.out.println(params);
////            JSONObject jsonObject = xingeApp.pushApp(params);
////            System.out.println(jsonObject.toString());
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//
//
//}
