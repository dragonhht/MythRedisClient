package redis.manager.controller;

import com.redis.common.exception.ReadConfigException;
import com.redis.config.*;
import com.redis.utils.MythReflect;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import redis.manager.Main;

import java.util.HashMap;
import java.util.Map;

/**
 * 连接设置Controller.
 * User: huang
 * Date: 17-6-22
 */
public class ConnectController {

    private PoolManagement poolManagement = Main.getManagement();
    private Stage dialogStage;
    private boolean okChecked = false;
    /** 标志是否是新建连接. */
    private static boolean flag = true;
    /** 最大连接数输入框. */
    @FXML
    private TextField maxActiveText;
    /** 最大闲置数输入框. */
    @FXML
    private TextField maxIdleText;
    /** 连接等待的最长时间. */
    @FXML
    private TextField maxWaitMillsText;
    /** 端口输入框. */
    @FXML
    private TextField portText;
    /** 服务器输入框. */
    @FXML
    private TextField hostText;
    /** 连接名称输入框. */
    @FXML
    private TextField nameText;
    /** 密码输入框. */
    @FXML
    private PasswordField passwordText;
    /** 密码确认框. */
    @FXML
    private PasswordField repasswordText;
    /** 最大连接数输入提醒. */
    @FXML
    private Label maxActiveLabel;
    /** 最大闲置数输入提醒. */
    @FXML
    private Label maxIdleLabel;
    /** 连接等待的最长时间输入提醒. */
    @FXML
    private Label maxWaitMillsLabel;
    /** 端口输入提醒. */
    @FXML
    private Label portLabel;
    /** 确认密码输入提醒. */
    @FXML
    private Label passwordLabel;
    /** 连接提示信息. */
    @FXML
    private Label resultLabel;
    @FXML
    private TreeView<Label> treeView;
    /** 最大连接数输入是否为数字. */
    private final boolean[] isNumActive = {false};
    /** 最大闲置数输入是否为数字. */
    private final boolean[] isNumIdle = {false};
    /** 连接等待的最长时间输入是否为数字. */
    private final boolean[] isNumWait = {false};
    /** 端口输入是否为数字. */
    private final boolean[] isNumPort = {false};
    /** 两次密码是否输入一致. */
    private final boolean[] isEqPassword = {false};
    /** 输入的配置是否正确. */
    private boolean isRight = false;
    /** 连接名称. */
    private static String linkName = "默认连接";
    private String poolId;

    /**
     * 初始化.
     */
    @FXML
    private void initialize() {
        resultLabel.setTextFill(Color.RED);
        // 监听最大连接数输入
        textChangeListener(maxActiveText, maxActiveLabel, isNumActive);
        // 监听最大闲置数输入
        textChangeListener(maxIdleText, maxIdleLabel, isNumIdle);
        // 监听连接等待的最长时间输入
        textChangeListener(maxWaitMillsText, maxWaitMillsLabel, isNumWait);
        // 监听端口输入
        textChangeListener(portText, portLabel, isNumPort);
        // 监听密码确认
        confirmPassword(passwordText, repasswordText, passwordLabel, isEqPassword);

        // 修改属性时
        if (!flag) {
            setPoolAttribute();
        }

    }


    /**
     * 设置dialogStage
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * 点击确定.
     */
    @FXML
    private void handleOk() {
        // 是否符合输入规则
        if (isNumActive[0] && isNumIdle[0] && isNumWait[0] && isNumPort[0] && isEqPassword[0]) {
            if (flag) {
                // 新建连接

                RedisPools pools = inputPoolManagement();
                // 输入的配置可用
                if (pools != null) {
                    linkName = nameText.getText();
                    okChecked = true;
                    dialogStage.close();
                    return;
                }
                resultLabel.setText("配置错误");
            } else {

                // 修改连接
                System.out.println("修改: " + portText.getText());
                RedisPoolProperty property = getProperty();
                property.setPoolId(poolId);
                System.out.println("uiuiuiuiuiiiui"+property.toString());
                RedisPoolProperty.updateConfigFile(property);
                okChecked = true;
                dialogStage.close();
            }

        }

    }

    /**
     * 将数据转成Property.
     * @return RedisPoolProperty
     */
    private RedisPoolProperty getProperty() {
        RedisPoolProperty property = new RedisPoolProperty();
        Map<String,Object> maps = new HashMap<>();
        maps.put(Configs.MAX_ACTIVE,maxActiveText.getText());
        maps.put(Configs.MAX_IDLE, maxIdleText.getText());
        maps.put(Configs.HOST, hostText.getText());
        maps.put(Configs.MAX_WAIT_MILLIS, maxWaitMillsText.getText());
        maps.put(Configs.NAME, nameText.getText());
        maps.put(Configs.PASSWORD, passwordText.getText());
        maps.put(Configs.PORT, portText.getText());
        maps.put(Configs.TEST_ON_BORROW,false);
        maps.put(Configs.TIMEOUT,6000);
        for(String key:maps.keySet()){
            System.out.println(key+"-----"+maps.get(key));
        }
        try {
            property = (RedisPoolProperty) MythReflect.setFieldsValue(property,maps);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return property;
    }

    /**
     * 保存信息.
     * @return RedisPools
     */
    public RedisPools inputPoolManagement(){
        RedisPools pool = null;
        RedisPoolProperty property = getProperty();
        try {
            isRight = poolManagement.checkConnection(property);
            if (isRight) {
                return poolManagement.createRedisPoolAndConnection(property);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pool;
    }

    /**
     * 点击取消.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * 获取是否点击确定.
     * @return
     */
    public boolean isOkChecked() {
        return okChecked;
    }

    /**
     * 文本框输入数字监听.
     * @param field 文本框
     * @param label 信息提示
     */
    private void textChangeListener(TextField field, Label label, boolean[] ok) {
        field.focusedProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (!field.getText().matches("[0-9]*")) {
                    label.setText("请输入数字");
                    label.setTextFill(Color.rgb(255, 0, 0));
                    ok[0] = false;
                    return;
                }
                label.setText("");
                ok[0] = true;
            }
        );
    }

    /**
     * 确认两次密码是否一致.
     * @param password 密码输入框
     * @param rePassword 确认密码输入框
     */
    private void confirmPassword(PasswordField password, PasswordField rePassword,
                                 Label label, boolean[] ok) {
        rePassword.focusedProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (!rePassword.getText().equals(password.getText())) {
                    label.setText("两次密码不一致");
                    label.setTextFill(Color.rgb(255, 0, 0));
                    ok[0] = false;
                    return;
                }
                label.setText("");
                ok[0] = true;
            }
        );
    }

    /**
     * 连接测试.
     */
    @FXML
    private void test() {
        RedisPoolProperty property = getProperty();
        try {
            isRight = poolManagement.checkConnection(property);
        } catch (Exception e) {
            // 密码为空时有异常
            isRight = false;
        }
        if (isRight) {
            resultLabel.setText("成功");
            return;
        }
        resultLabel.setText("失败");
    }

    /**
     * 获取新连接的名称和id.
     * @return map
     */
    public static Map<String, String> getConnectMessage() {
        Map<String, String> map = new HashMap<>();
        map.put("name", linkName);
        String id = null;
        try {
            id = String.valueOf(PropertyFile.getMaxId());
        } catch (ReadConfigException e) {
            e.printStackTrace();
        }
        map.put("id", id);
        return map;
    }

    public static boolean isFlag() {
        return flag;
    }

    /**
     * 设置是否为新建连接.
     * @param flag true为新建连接
     */
    public static void setFlag(boolean flag) {
        ConnectController.flag = flag;
    }

    /**
     * 显示连接属性.
     */
    private void setPoolAttribute() {
        String poolId = poolManagement.getCurrentPoolId();
        RedisPoolProperty property = RedisPoolProperty.initByIdFromConfig(poolId);
        maxActiveText.setText(String.valueOf(property.getMaxActive()));
        maxIdleText.setText(String.valueOf(property.getMaxIdle()));
        maxWaitMillsText.setText(String.valueOf(property.getMaxWaitMills()));
        portText.setText(String.valueOf(property.getPort()));
        hostText.setText(property.getHost());
        nameText.setText(property.getName());
        passwordText.setText(property.getPassword());
        repasswordText.setText(property.getPassword());
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }
}
