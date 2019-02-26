package com.syhdoctor.webserver.service.kangyang;

import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.encryption.AESEncrypt;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.mapper.kangyang.KangyangMapper;
import com.syhdoctor.webserver.service.code.CodeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class KangyangBaseService extends BaseService {
    @Autowired
    private KangyangMapper kangyangMapper;

    @Autowired
    private CodeService codeService;

    public void importKangyangUser(String filename, int startx, int starty, int maxColumn, int sheetCount) {
        List<Map<String, Object>> list = ExcelUtil.readExcel(filename, startx, starty, maxColumn, sheetCount);
        if (list != null) {
            for (Map<String, Object> map : list) {
                String phone = ModelUtil.getStr(map, "5");
                try {
                    String name = ModelUtil.getStr(map, "0");
                    String vip = ModelUtil.getStr(map, "1");
                    String bir = ModelUtil.getStr(map, "2");
                    long birlong = UnixUtil.dateTimeStamp(bir, "yyyy-MM-dd");
                    int age = ModelUtil.getInt(map, "3");
                    String gandername = ModelUtil.getStr(map, "4");
                    int gander = 9;
                    if ("男".equals(gandername)) {
                        gander = 1;
                    } else if ("女".equals(gandername)) {
                        gander = 2;
                    }
                    String cardno = ModelUtil.getStr(map, "6");
                    String address = ModelUtil.getStr(map, "7");
                    String id = ModelUtil.getStr(map, "8");
                    Map<String, Object> user = kangyangMapper.getUser(phone);
                    if (user == null) {
                        kangyangMapper.importKangyangUser(name, "", vip, birlong, age, gander, phone, cardno, "", address, id);
                    } else {
                        log.error("该号码已经存在>>" + phone);
                    }
                } catch (Exception e) {
                    log.error("用户导入失败>>" + phone);
                }
            }
        }
    }

    public Map<String, Object> useInternetHospital(String name, String headpic, String vip, String birthday, String gender, String cardno, String address, String phone, String id, int age) {
        Map<String, Object> user = kangyangMapper.getUser(phone);
        long birlong = UnixUtil.dateTimeStamp(birthday, "yyyy-MM-dd");
        int genderInt = 9;
        if ("男".equals(gender)) {
            genderInt = 1;
        } else if ("女".equals(gender)) {
            genderInt = 2;
        }
        List<Map<String, String>> maps = addressResolution(address);
        String areas = "";
        if (maps.size() > 0) {
            String province = ModelUtil.getStr(maps.get(0), "province");
            String city = ModelUtil.getStr(maps.get(0), "city");
            String county = ModelUtil.getStr(maps.get(0), "county");

            String pcode = ModelUtil.getStr(codeService.getAreaByValue(province), "code");
            String ccode = ModelUtil.getStr(codeService.getAreaByValue(city), "code");
            String dcode = ModelUtil.getStr(codeService.getAreaByValue(county), "code");
            if (!StrUtil.isEmpty(pcode, ccode, dcode)) {
                areas = String.format("%s,%s,%s", pcode, ccode, dcode);
            }
        }
        if (StrUtil.isEmpty(headpic)) {
            headpic = "defaultuserprofile.png";
        }
        long userId;
        int flag;
        if (user == null) {
            flag = 1;
            userId = kangyangMapper.importKangyangUser(name, headpic, vip, birlong, age, genderInt, phone, cardno, areas, address, id);
        } else {
            flag = 0;
            userId = ModelUtil.getLong(user, "id");
            kangyangMapper.updateKangyangUser(name, headpic, vip, birlong, age, genderInt, cardno, areas, address, ModelUtil.getLong(user, "id"));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("userid", AESEncrypt.getInstance().encrypt(String.valueOf(userId)));
        result.put("flag", flag);
        return result;
    }

    public void aaa() {
        List<Map<String, Object>> list = ExcelUtil.readExcel("/home/qwq/tools/hos.xls", 1, 1, 8, 1);
        if (list != null) {
            for (Map<String, Object> map : list) {
                String name = ModelUtil.getStr(map, "1");
                String pname = ModelUtil.getStr(map, "2");
                String pid = ModelUtil.getStr(codeService.getAreaByValue(pname), "id");
                String cname = ModelUtil.getStr(map, "3");
                String cid = ModelUtil.getStr(codeService.getAreaByValue(cname), "id");
                String dname = ModelUtil.getStr(map, "4");
                String did = null;
                String address = ModelUtil.getStr(map, "5");
                if (StrUtil.isEmpty(dname)) {
                    List<Map<String, String>> maps = addressResolution(address);
                    if (maps.size() > 0) {
                        Map<String, String> stringStringMap = maps.get(0);
                        dname = ModelUtil.getStr(stringStringMap, "county");
                        did = ModelUtil.getStr(codeService.getAreaByValue(dname), "id");
                    }
                } else {
                    did = ModelUtil.getStr(codeService.getAreaByValue(dname), "id");
                }
                int type = ModelUtil.getInt(map, "6");
                int level = ModelUtil.getInt(map, "7");
                try {
                    kangyangMapper.importHospital(name, pid, cid, did, address, type, level);
                } catch (Exception e) {
                    log.error("用户导入失败>>" + name);
                }
            }
        }
    }

    public void bbb() {
        List<Map<String, Object>> hospitalList = kangyangMapper.getHospitalList();
        for (Map<String, Object> map : hospitalList) {
            String hospital_name = Pinyin4jUtil.getInstance().getFirstSpell(ModelUtil.getStr(map, "hospital_name")).toUpperCase();
            kangyangMapper.updateHospital(hospital_name, ModelUtil.getLong(map, "id"));
        }
    }


    public static void main(String[] args) {
        String regex = "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
        Matcher m = Pattern.compile(regex).matcher("东西省");
        System.out.println(m.find());
    }

    /**
     * 解析地址
     *
     * @param address
     * @return
     * @author lin
     */
    public static List<Map<String, String>> addressResolution(String address) {
        String regex = "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
        Matcher m = Pattern.compile(regex).matcher(address);
        String province = null, city = null, county = null, town = null, village = null;
        List<Map<String, String>> table = new ArrayList<Map<String, String>>();
        Map<String, String> row = null;
        while (m.find()) {
            row = new LinkedHashMap<String, String>();
            province = m.group("province");
            row.put("province", province == null ? "" : province.trim());
            city = m.group("city");
            row.put("city", city == null ? "" : city.trim());
            county = m.group("county");
            row.put("county", county == null ? "" : county.trim());
            town = m.group("town");
            row.put("town", town == null ? "" : town.trim());
            village = m.group("village");
            row.put("village", village == null ? "" : village.trim());
            table.add(row);
        }
        return table;
    }

    public String jump(String phone, int jumptype) {
        Map<String, Object> user = kangyangMapper.getUser(phone);
        String redirecturl = ConfigModel.WEBLINKURL + "web/syhdoctor/#/download";
        if (user != null) {
            String id = AESEncrypt.getInstance().encrypt(ModelUtil.getStr(user, "id"));
            if (jumptype == 1) {
                //大学
                redirecturl = String.format(ConfigModel.WEBLINKURL + "web/syhdoctor/#/university?uid=%s", id);
            } else if (jumptype == 2) {
                //头条
                redirecturl = String.format(ConfigModel.WEBLINKURL + "web/syhdoctor/#/headline?uid=%s", id);
            } else if (jumptype == 3) {
                //电话
                redirecturl = String.format(ConfigModel.WEBLINKURL + "web/syhdoctor/#/phoneadvisory?uid=%s", id);
            } else if (jumptype == 4) {
                //电话订单列表
                redirecturl = String.format(ConfigModel.WEBLINKURL + "web/syhdoctor/#/lyyinquiry?uid=%s&ordertype=2", id);
            } else if (jumptype == 5) {
                //绿通
                redirecturl = String.format(ConfigModel.WEBLINKURL + "web/syhdoctor/#/greenregistered?uid=%s", id);
            } else if (jumptype == 6) {
                //图文
                redirecturl = String.format(ConfigModel.WEBLINKURL + "web/syhdoctor/#/inquiryadvisory?uid=%s", id);
            } else if (jumptype == 7) {
                //图文订单
                redirecturl = String.format(ConfigModel.WEBLINKURL + "web/syhdoctor/#/lyyinquiry?uid=%s&ordertype=1", id);
            }
        }
        return redirecturl;
    }

}
