package redis.manager;

import com.redis.assemble.hash.RedisHash;
import com.redis.assemble.key.RedisKey;
import com.redis.assemble.list.RedisList;
import com.redis.assemble.set.RedisSet;
import com.redis.assemble.set.sort.RedisSortSet;
import com.redis.common.exception.ReadConfigException;
import com.redis.config.PoolManagement;
import com.redis.config.PropertyFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import redis.manager.controller.ConnectController;
import redis.manager.controller.MainController;
import redis.manager.controller.operation.panel.ConnectPanel;

import java.util.Map;


/**
 * 应用主程序.
 *
 */

public class Main extends Application {

    private static PoolManagement management = PoolManagement.getInstance();
    private static RedisKey redisKey = RedisKey.getInstance();
    private static RedisList redisList = RedisList.getInstance();
    private static RedisSet redisSet = RedisSet.getInstance();
    private static RedisHash redisHash = RedisHash.getInstance();
    private static RedisSortSet redisSortSet = RedisSortSet.getInstance();

    private AnchorPane rootLayout;
    private FXMLLoader rootLoader;
    private Stage primaryStage;
    /** 选择的键. */
    private String selectedKey;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Redis客户端");

        rootLoader = new FXMLLoader();
        rootLoader.setLocation(this.getClass().getResource("/views/MainLayout.fxml"));
        rootLayout = rootLoader.load();

        MainController mainController = rootLoader.getController();
        mainController.setMain(this);

        Scene scene = new Scene(rootLayout);
        this.primaryStage.setScene(scene);
        this.primaryStage.setWidth(1300);
        this.primaryStage.setHeight(700);
        this.primaryStage.setResizable(false);
        this.primaryStage.show();

    }


    /**
     * 显示连接设置窗口.
     * @return 是否点击了确定, true为已点击
     */
    public boolean showConnectPanel() {
        boolean ok = false;
        // 显示面板
        ConnectPanel connectPanel = new ConnectPanel();
        connectPanel.isNewLink(true);
        ok = connectPanel.showConnectPanel(null);

        if (ok) {
            // 更新连接信息
            MainController mainController = rootLoader.getController();
            Map<String, String> map = ConnectController.getConnectMessage();
            String name = map.get("name");
            String id = map.get("id");
            mainController.updateTree(name, id);
        }


        return ok;
    }

    public String getSelectedKey() {
        return selectedKey;
    }

    public void setSelectedKey(String selectedKey) {
        this.selectedKey = selectedKey;
    }

    public static void main(String[] args ) throws ReadConfigException {
        management.setCurrentPoolId(PropertyFile.getMaxId()+"");
        launch(args);

    }

    public static PoolManagement getManagement() {
        return management;
    }

    public static RedisKey getRedisKey() {
        return redisKey;
    }

    public static RedisList getRedisList() {
        return redisList;
    }

    public static RedisSet getRedisSet() {
        return redisSet;
    }

    public static RedisHash getRedisHash() {
        return redisHash;
    }

    public static RedisSortSet getRedisSortSet() {
        return redisSortSet;
    }
}
