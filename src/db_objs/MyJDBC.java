package db_objs;

import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

/*
    JDBC class is used to interact with our MySQL Database to perform activities such as retrieving and updating our db
 */
public class MyJDBC {
    // database configurations
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/bankapp";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Qwer1234";

    // if valid return an object with the user's information
    public static User validateLogin(String username, String password){
        try{
            // establish a connection to the database using configurations
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            // create sql query
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password = ?"
            );

            // replace the ? with values
            // parameter index referring to the iteration of the ? so 1 is the first ? and 2 is the second ?
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // execute query and store into a result set
            ResultSet resultSet = preparedStatement.executeQuery();

            // next() returns true or false
            // true - query returned data and result set now points to the first row
            // false - query returned no data and result set equals to null
            if(resultSet.next()){
                // success
                // get id
                int userId = resultSet.getInt("id");

                // get current balance
                BigDecimal currentBalance = resultSet.getBigDecimal("current_balance");

                // return user object
                return new User(userId, username, password, currentBalance);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        // not valid user
        return null;
    }

    // registers new user to the database
    // true - register success
    // false - register fails
    public static boolean register(String username, String password){
        try{
            // first we will need to check if the username has already been taken
            if(!checkUser(username)){
                Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO users(username, password, current_balance) " +
                                "VALUES(?, ?, ?)"
                );

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setBigDecimal(3, new BigDecimal(0));

                preparedStatement.executeUpdate();
                return true;

            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    // check if username already exists in the db
    // true - user exists
    // false - user doesn't exist
    private static boolean checkUser(String username){
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?"
            );

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            // this means that the query returned no data meaning that the username is available
            if(!resultSet.next()){
                return false;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return true;
    }

    // true - update to db was a success
    // false - update to the db was a fail
    public static boolean addTransactionToDatabase(Transaction transaction){
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            PreparedStatement insertTransaction = connection.prepareStatement(
                "INSERT transactions(user_id, transaction_type, transaction_amount, transaction_date) " +
                        "VALUES(?, ?, ?, NOW())"
            );
            // NOW() will put in the current date

            insertTransaction.setInt(1, transaction.getUserId());
            insertTransaction.setString(2, transaction.getTransactionType());
            insertTransaction.setBigDecimal(3, transaction.getTransactionAmount());

            // update database
            insertTransaction.executeUpdate();

            return true;

        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    // true - update balance successful
    // false - update balance fail
    public static boolean updateCurrentBalance(User user){
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            PreparedStatement updateBalance = connection.prepareStatement(
                    "UPDATE users SET current_balance = ? WHERE id = ?"
            );

            updateBalance.setBigDecimal(1, user.getCurrentBalance());
            updateBalance.setInt(2, user.getId());

            updateBalance.executeUpdate();
            return true;

        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    // true - transfer was a success
    // false - transfer was a fail
    public static boolean transfer(User user, String transferredUsername, float transferAmount){
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            PreparedStatement queryUser = connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?"
            );

            queryUser.setString(1, transferredUsername);
            ResultSet resultSet = queryUser.executeQuery();

            while (resultSet.next()) {
                // perfrom transfer
                User transferredUser = new User(
                        resultSet.getInt("id"),
                        transferredUsername,
                        resultSet.getString("password"),
                        resultSet.getBigDecimal("current_balance")
                );

                // create transaction
                Transaction transferTransaction = new Transaction(
                        user.getId(),
                        "Transfer",
                        new BigDecimal(-transferAmount),
                        null
                );

                // this transaction will belong to the transferred user
                Transaction receivedTransaction = new Transaction(
                        transferredUser.getId(),
                        "Transfer",
                        new BigDecimal(transferAmount),
                        null
                );

                // update transfer user
                transferredUser.setCurrentBalance(transferredUser.getCurrentBalance().add(BigDecimal.valueOf(transferAmount)));
                updateCurrentBalance(transferredUser);

                // update user current balance
                user.setCurrentBalance(user.getCurrentBalance().subtract(BigDecimal.valueOf(transferAmount)));
                updateCurrentBalance(user);

                // add these transactions to the database
                addTransactionToDatabase(transferTransaction);
                addTransactionToDatabase(receivedTransaction);

                return true;

            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取所有过去交易记录
     * 该方法主要用于获取用户的历史交易记录
     *
     * @param user 用户对象，包含用户ID等信息
     * @return 返回一个Transaction对象的ArrayList，包含该用户的全部历史交易记录
     */
    public static ArrayList<Transaction> getPastTransaction(User user){
        // 创建一个ArrayList来存储历史交易记录
        ArrayList<Transaction> pastTransactions = new ArrayList<>();
        try{
            // 建立数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            // 准备SQL查询语句，根据user_id查询transactions表中的记录
            PreparedStatement selectAllTransaction = connection.prepareStatement(
                "SELECT * FROM transactions WHERE user_id = ?"
            );
            // 设置查询参数，即用户的ID
            selectAllTransaction.setInt(1, user.getId());

            // 执行查询并获取结果集
            ResultSet resultSet = selectAllTransaction.executeQuery();

            // 遍历结果集
            while(resultSet.next()){
                // 根据结果集创建交易对象
                Transaction transaction = new Transaction(
                        user.getId(),
                        resultSet.getString("transaction_type"),
                        resultSet.getBigDecimal("transaction_amount"),
                        resultSet.getDate("transaction_date")
                );

                // 将交易对象添加到ArrayList中
                pastTransactions.add(transaction);
            }
        }catch(SQLException e){
            // 处理SQL异常
            e.printStackTrace();
        }

        // 返回历史交易记录列表
        return pastTransactions;
    }

}












