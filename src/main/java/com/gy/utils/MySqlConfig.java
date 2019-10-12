package com.gy.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * created by yangyu on 2019-10-08
 */
@Configuration
@PropertySource("classpath:mysql.properties")
@MapperScan(basePackages = "com.gy.dao", sqlSessionTemplateRef = "icSqlSessionTemplate")
public class MySqlConfig {

    private static final Logger logger = LoggerFactory.getLogger(MySqlConfig.class);

    @Value(value = "${jdbc.url}")
    private String url;

    @Value(value = "${jdbc.user}")
    private String username;

    @Value(value = "${jdbc.pwd}")
    private String password;

    @Value(value = "${jdbc.maxwait}")
    private int maxwait;

    @Value(value = "${jdbc.className}")
    private String driverClassName;

    @Value(value = "${jdbc.validationQuery}")
    private String validationQuery;

    @Value(value = "${jdbc.maxActive}")
    private int maxActive;

    @Value(value = "${jdbc.initialSize}")
    private int initialSize;

    @Value(value = "${jdbc.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;

    @Value(value = "${jdbc.testOnBorrow}")
    private boolean testOnBorrow;

    @Value(value = "${jdbc.testOnReturn}")
    private boolean testOnReturn;

    @Value(value = "${jdbc.testWhileIdle}")
    private boolean testWhileIdle;

    @Value(value = "${jdbc.filters}")
    private String filters;


    @Value(value = "${white.iplist}")
    private String whiteIpList;

    @Value(value = "${black.iplist}")
    private String blackIpList;

    @Value(value = "${login.username}")
    private String loginUsername;

    @Value(value = "${login.password}")
    private String loginPassword;

    @Value(value = "${reset.enable}")
    private String resetEnable;

    @Value(value = "${druid.stat.view.path}")
    private String druidStatViewPath;

    @Value(value = "${url.pattern}")
    private String urlPattern;

    @Value(value = "${exclusions}")
    private String exclusions;

    private static final String ALLOW = "allow";
    private static final String DENY = "deny";
    private static final String LOGIN_USERNAME = "loginUsername";
    private static final String LOGIN_PASSWORD = "loginPassword";
    private static final String RESET_ENABLE = "resetEnable";
    private static final String EXCLUSIONS = "exclusions";

    @Bean(name = "icDataSource")
    @Primary
    public DataSource icDataSource() throws Exception{
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        // 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：监控统计用的filter:stat、日志用的filter:log4j、防御sql注入的filter:wall
        dataSource.setFilters(filters);
        dataSource.setMaxWait(maxwait);

        //用来检测连接是否有效
        dataSource.setValidationQuery(validationQuery);
        //申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        dataSource.setTestOnBorrow(testOnBorrow);
        //归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        dataSource.setTestOnReturn(testOnReturn);
        //申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        //如果检测失败，则连接将被从池中去除
        dataSource.setTestWhileIdle(testWhileIdle);

        //最大连接池数量
        dataSource.setMaxActive(maxActive);
        // 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
        dataSource.setInitialSize(initialSize);

        // 通过datasource.getConnontion() 取得的连接必须在removeAbandonedTimeout这么多秒内调用close(),要不我就弄死你.(就是conn不能超过指定的租期)
        //Druid提供了RemoveAbandanded相关配置，用来关闭长时间不使用的连接
        // 打开removeAbandoned功能
//        dataSource.setRemoveAbandoned(true);
        // 1800秒，也就是30分钟
//        dataSource.setRemoveAbandonedTimeout(1800);
        // 关闭abanded连接时输出错误日志
//        dataSource.setLogAbandoned(true);

        return dataSource;
    }

    @Bean(name = "icSqlSessionFactory")
    @Primary
    public SqlSessionFactory icSqlSessionFactory(@Qualifier("icDataSource") DataSource icDataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(icDataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mybatis/*.xml"));
        bean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        return bean.getObject();
    }

    @Bean(name = "icTransactionManager")
    @Primary
    public DataSourceTransactionManager icTransactionManager(@Qualifier("icDataSource") DataSource icDataSource){
        return new DataSourceTransactionManager(icDataSource);
    }

    @Bean(name = "icSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate icSqlSessionTemplate(@Qualifier("icSqlSessionFactory")
                                                                SqlSessionFactory icSqlSessionFactory) throws Exception{
        return new SqlSessionTemplate(icSqlSessionFactory);
    }

    /**
     * <p>配置监控服务器</p>
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), druidStatViewPath);
        if (StringUtils.isNotBlank(whiteIpList)) {
            // 添加IP白名单
            servletRegistrationBean.addInitParameter(ALLOW, whiteIpList);
        }
        if (StringUtils.isNotBlank(blackIpList)) {
            // 添加IP黑名单，当白名单和黑名单重复时，黑名单优先级更高
            servletRegistrationBean.addInitParameter(DENY, blackIpList);
        }
        // 添加控制台管理用户
        servletRegistrationBean.addInitParameter(LOGIN_USERNAME, loginUsername);
        servletRegistrationBean.addInitParameter(LOGIN_PASSWORD, loginPassword);
        // 是否能够重置数据
        servletRegistrationBean.addInitParameter(RESET_ENABLE, resetEnable);
        return servletRegistrationBean;
    }

    /**
     * <p>配置服务过滤器</p>
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean statFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        // 添加过滤规则
        filterRegistrationBean.addUrlPatterns(urlPattern);
        // 忽略过滤格式
        filterRegistrationBean.addInitParameter(EXCLUSIONS, exclusions);
        return filterRegistrationBean;
    }

}
