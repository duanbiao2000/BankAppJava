package db_objs;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * 交易实体用于存储交易数据
 */
public class Transaction {
    // 用户ID，标识进行交易的用户的唯一标识
    private final int userId;
    // 交易类型，描述交易的类别或目的
    private final String transactionType;
    // 交易金额，涉及交易的金额
    private final BigDecimal transactionAmount;
    // 交易日期，交易发生的时间
    private final Date transactionDate;

    /**
     * 构造一个交易对象
     *
     * @param userId            进行交易的用户ID
     * @param transactionType   交易类型
     * @param transactionAmount 交易金额
     * @param transactionDate   交易日期
     */
    public Transaction(int userId, String transactionType, BigDecimal transactionAmount, Date transactionDate){
        this.userId = userId;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
    }

    /**
     * 获取交易的用户ID
     *
     * @return 用户ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 获取交易类型
     *
     * @return 交易类型
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * 获取交易金额
     *
     * @return 交易金额
     */
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * 获取交易日期
     *
     * @return 交易日期
     */
    public Date getTransactionDate() {
        return transactionDate;
    }
}
