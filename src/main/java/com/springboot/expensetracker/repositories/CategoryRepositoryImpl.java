package com.springboot.expensetracker.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import com.springboot.expensetracker.domain.Category;
import com.springboot.expensetracker.exceptions.EtBadRequestException;
import com.springboot.expensetracker.exceptions.EtResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class CategoryRepositoryImpl  implements CategoryRepository{

    public static final String SQL_CREATE = "INSERT INTO ET_CATEGORIES (CATEGORY_ID, USER_ID, TITLE, DESCRIPTION) VALUES(NEXTVAL('ET_CATEGORIES_SEQ'),?,?,?);";
    public static final String SQL_FIND_BY_ID = "SELECT C.CATEGORY_ID, C.USER_ID, C.TITLE, C.DESCRIPTION, COALESCE(SUM(T.AMOUNT),0) TOTAL_EXPENSE "+
                                                "FROM ET_TRANSACTIONS T RIGHT OUTER JOIN ET_CATEGORIES C ON C.CATEGORY_ID = T.CATEGORY_ID "+
                                                "WHERE C.USER_ID = ? AND C.CATEGORY_ID = ? GROUP BY C.CATEGORY_ID";
    public static final String SQL_FIND_ALL = "SELECT C.CATEGORY_ID, C.USER_ID, C.TITLE, C.DESCRIPTION, COALESCE(SUM(T.AMOUNT),0) TOTAL_EXPENSE "+
                                            "FROM ET_TRANSACTIONS T RIGHT OUTER JOIN ET_CATEGORIES C ON C.CATEGORY_ID = T.CATEGORY_ID "+
                                            "WHERE C.USER_ID = ? GROUP BY C.CATEGORY_ID";
    public static final String SQL_UPDATE = "UPDATE ET_CATEGORIES SET TITLE = ?, DESCRIPTION = ? WHERE USER_ID = ? AND CATEGORY_ID = ?";
    public static final String SQL_REMOVE_CATEGORY  = "DELETE FROM ET_CATEGORIES WHERE USER_ID = ? AND CATEGORY_ID=?;";
    public static final String SQL_REMOVE_TRANSACTIONS  = "DELETE FROM ET_TRANSACTIONS WHERE CATEGORY_ID=?;";


    @Autowired 
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Category> findAll(Integer userId) throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.query(SQL_FIND_ALL, categoryRowMapper, new Object[]{userId});
        } catch (Exception e) {
            //System.out.println(e);
            throw new EtResourceNotFoundException("Category not found");
        }
    }

    @Override
    public Category findById(Integer userId, Integer categoryId) throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, categoryRowMapper, new Object[]{userId,categoryId});
        } catch (Exception e) {
            //System.out.println(e);
            throw new EtResourceNotFoundException("Category not found");
        }
    }

    @Override
    public Integer create(Integer userId, String title, String description) throws EtBadRequestException {
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,userId);
                ps.setString(2,title);
                ps.setString(3, description);
                return ps;
            },keyHolder);
            return (Integer) keyHolder.getKeys().get("CATEGORY_ID");
        }catch (Exception e){
            //System.out.println(e);
            throw new EtBadRequestException("Invalid request");
        }
        
    }

    @Override
    public void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException {
        try {
            jdbcTemplate.update(SQL_UPDATE, new Object[]{category.getTitle(),category.getDescription(),userId,categoryId});
        } catch (Exception e) {
            //System.out.println(e);
            throw new EtResourceNotFoundException("Invalid Request");
        }
        
    }

    @Override
    public void removeById(Integer userId, Integer categoryId) {
        removeAllCatTransactions(categoryId);
        try {
            jdbcTemplate.update(SQL_REMOVE_CATEGORY, new Object[]{userId,categoryId});
        } catch (Exception e) {
            //System.out.println(e);
            throw new EtResourceNotFoundException("Invalid Request");
        }
    }

    private void removeAllCatTransactions(Integer categoryId){
        jdbcTemplate.update(SQL_REMOVE_TRANSACTIONS, new Object[]{categoryId});
    }


    private RowMapper<Category> categoryRowMapper = ((rs,rowNum) -> {
        return new Category(rs.getInt("CATEGORY_ID"),
                rs.getInt("USER_ID"),
                rs.getString("TITLE"),
                rs.getString("DESCRIPTION"),
                rs.getDouble("TOTAL_EXPENSE")
        );
    });
    
}
