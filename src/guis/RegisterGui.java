package guis;
import db_objs.MyJDBC;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 注册界面类，继承自BaseFrame，用于显示注册界面并处理注册逻辑
 */
public class RegisterGui extends BaseFrame {
    
    /**
     * 构造函数，初始化注册界面的标题
     */
    public RegisterGui() {
        super("Banking App Register");
    }

    /**
     * 添加注册界面的组件，包括标签、文本框和按钮
     */
    @Override
    protected void addGuiComponents() {
        // 创建并添加“Banking Application”标签
        JLabel bankingAppLabel = new JLabel("Banking Application");
        bankingAppLabel.setBounds(0, 20, super.getWidth(), 40);
        bankingAppLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        bankingAppLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(bankingAppLabel);

        // 创建并添加“Username”标签和文本框
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(20, 120, getWidth() - 30, 24);
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(usernameLabel);
        
        JTextField usernameField = new JTextField();
        usernameField.setBounds(20, 160, getWidth() - 50, 40);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(usernameField);

        // 创建并添加“Password”标签和密码框
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 220, getWidth() - 50, 24);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(passwordLabel);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(20, 260, getWidth() - 50, 40);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(passwordField);

        // 创建并添加“Re-type Password”标签和密码框
        JLabel rePasswordLabel = new JLabel("Re-type Password:");
        rePasswordLabel.setBounds(20, 320, getWidth() - 50, 40);
        rePasswordLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(rePasswordLabel);
        
        JPasswordField rePasswordField = new JPasswordField();
        rePasswordField.setBounds(20, 360, getWidth() - 50, 40);
        rePasswordField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(rePasswordField);

        // 创建并添加“Register”按钮，绑定点击事件
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(20, 460, getWidth() - 50, 40);
        registerButton.setFont(new Font("Dialog", Font.BOLD, 20));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                String rePassword = String.valueOf(rePasswordField.getPassword());
                
                if (validateUserInput(username, password, rePassword)) {
                    if (MyJDBC.register(username, password)) {
                        RegisterGui.this.dispose();
                        LoginGui loginGui = new LoginGui();
                        loginGui.setVisible(true);
                        JOptionPane.showMessageDialog(loginGui, "Registered Account Successfully!");
                    } else {
                        JOptionPane.showMessageDialog(RegisterGui.this, "Error: Username already taken");
                    }
                } else {
                    JOptionPane.showMessageDialog(RegisterGui.this,
                            "Error: Username must be at least 6 characters\n" +
                            "and/or Password must match");
                }
            }
        });
        add(registerButton);

        // 创建并添加“Have an account? Sign-in here”标签，绑定鼠标点击事件
        JLabel loginLabel = new JLabel("<html><a href=\"#\">Have an account? Sign-in here</a></html>");
        loginLabel.setBounds(0, 510, getWidth() - 10, 30);
        loginLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterGui.this.dispose();
                new LoginGui().setVisible(true);
            }
        });
        add(loginLabel);
    }

    /**
     * 验证用户输入的合法性
     * 
     * @param username 用户名
     * @param password 密码
     * @param rePassword 确认密码
     * @return 如果用户名和密码符合要求，则返回true；否则返回false
     */
    private boolean validateUserInput(String username, String password, String rePassword) {
        if (username.length() == 0 || password.length() == 0 || rePassword.length() == 0) return false;
        if (username.length() < 6) return false;
        if (!password.equals(rePassword)) return false;
        return true;
    }
}
