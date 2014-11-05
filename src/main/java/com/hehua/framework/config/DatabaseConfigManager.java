/**
 * 
 */
package com.hehua.framework.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hehua.Hehua;
import com.hehua.framework.config.database.DatabaseConfigDAO;
import com.hehua.framework.config.database.DatabaseConfigLocalCache;
import com.hehua.framework.subscribe.ZookeeperPubSubService;

/**
 * @author zhihua
 *
 */
@Component
public class DatabaseConfigManager implements ConfigManager {

    @Autowired
    private DatabaseConfigDAO databaseConfigDAO;

    @Autowired
    private DatabaseConfigLocalCache databaseConfigLocalCache;

    @Override
    public Map<String, String> getAll() {
        return databaseConfigLocalCache.get();
    }

    @Override
    public String getString(String key) {
        Map<String, String> configs = getAll();
        return configs.get(key);
    }

    @Override
    public void setString(String key, String value) {
        databaseConfigDAO.update(key, value);
        ZookeeperPubSubService.getInstance().post(databaseConfigLocalCache.key(), "update");
    }

    public static void main(String[] args) {

        Hehua.init();
        DatabaseConfigManager configManager = Hehua.getContext().getBean(
                DatabaseConfigManager.class);
        System.out.println(configManager.getAll());

        configManager
                .setString(
                        "order_tips",
                        "什么时候发货：\n支付成功后，将在2小时内发货。\n\n退货政策：\n只要您不满意，90天内我们退货。\n\n免运费规则：\n单笔订单价格超过158元则免除8元运费，湿巾及尿不湿不在免运费规则内。");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 1; i < 100; i++) {
            System.out.println(configManager.getAll());
        }
    }
}
