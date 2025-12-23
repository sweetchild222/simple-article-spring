package net.inkuk.simple_article;


import jakarta.annotation.PostConstruct;
import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class SimpleArticleApplication implements ApplicationRunner {

    //DataBaseClient client = new DataBaseClient();

	public static void main(String[] args) {

        SpringApplication.run(SimpleArticleApplication.class, args);

        Log.info("asdfsa");

        //appContext.getBean()

        //MyService myService = appContext.getBean(MyService.class);
        //myService.doSomething();
        //appContext.close(); // Close the context when done

        //SpringApplication app = new SpringApplication(SimpleArticleApplication.class);

        //app.run(args);





        //UserService userService = new UserService();

        //List<User> a = userService.findUser("asdf");
        //sSystem.out.println(a);



	}


    @Override
    public void run(ApplicationArguments args) throws Exception {



    }


    @PostConstruct
    public void init() {

        //System.out.println("asdfsa");
    }


    public void sqltest(int index){

//        DataBaseClient client = DataBaseClientPool.getInstance().getClient();
//
//        try {
//
//            System.out.println("----------------------" + String.valueOf(index));
//            String query = "select * from user";
//
//            ResultSet resultSet = client.createResultSet(query);
//
//            if(resultSet == null)
//                return;
//
//            while (resultSet.next()) {
//                String userId = resultSet.getString(1);
//                System.out.println(String.valueOf(index) + userId);
//                String password = resultSet.getString(2);
//                System.out.println(String.valueOf(index) + password);
//                String name = resultSet.getString(3);
//                System.out.println(String.valueOf(index) + name);
//            }
//
//            resultSet.close();
//
//            //DataBaseClientPool.getInstance().close();
//        }
//        catch(SQLException e){
//
//            System.out.println(e.toString());
//        }
    }



    @Scheduled(fixedRate = 1000)
    public void test() {

        //sqltest(1);
    }

//    @Scheduled(fixedRate = 10)
//    public void test2() {
//
//        sqltest(2);
//    }
//
//
//
//    @Scheduled(fixedRate = 10)
//    public void test3() {
//
//        sqltest(3);
//    }
//
//
//
//    @Scheduled(fixedRate = 10)
//    public void test4() {
//
//        sqltest(1);
//
//    }

}
