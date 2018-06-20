package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import com.mysql.jdbc.MysqlErrorNumbers;

import springbook.user.domain.Level;
import springbook.user.domain.User;


public class UserDaoJdbc implements UserDao {
	private static final Logger logger = LoggerFactory.getLogger(UserDaoJdbc.class);
	
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};
	
	public void add(final User user) {
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend) "
				               + "values(?,?,?,?,?,?)", 
				                 user.getId(), 
				                 user.getName(), 
				                 user.getPassword(),
				                 user.getLevel().intValue(),
				                 user.getLogin(),
				                 user.getRecommend());
	}

	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?", 
				new Object[] {id}, this.userMapper);
	}
	
	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", 
				this.userMapper);
	}
	
	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}
	
	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}
	
	public interface StatementStrategy {
		PreparedStatement makePreparedStatement(Connection c) throws SQLException;
	}
	
	public class DuplicateUserIdException extends RuntimeException {
		public DuplicateUserIdException(Throwable cause) {
			super(cause);
		}
	}
	
	
}
