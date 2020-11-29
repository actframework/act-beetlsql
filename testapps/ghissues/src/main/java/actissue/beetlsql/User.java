package actissue.beetlsql;

import act.cli.Command;
import act.db.beetlsql.BeetlSqlDao;
import act.db.beetlsql.BeetlSqlTransactional;
import act.db.sql.tx.Transactional;
import act.util.SimpleBean;
import org.osgl.http.H;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.With;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "user")
public class User implements SimpleBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String name;

    public static class UserService extends BeetlSqlDao<Integer, User> {

        @Command("user.add")
        public User create(String name) {
            User user = new User();
            user.name = name;
            return save(user);
        }

        @Command("user.update")
        @Transactional
        public void update(Integer id, String name) {
            doUpdate(id, name);
        }

        @Command("user.update2")
        @Transactional
        public void update2(Integer id, String name, H.Response resp) {
            doUpdate(id, name);
            resp.contentType("text/plaintext");
            resp.writeText("done");
            resp.close();
        }

        @Command("user.update3")
        @With(BeetlSqlTransactional.class)
        public void update3(Integer id, String name, H.Response resp) {
            doUpdate(id, name);
        }

        @Command("user.update4")
        @With(BeetlSqlTransactional.class)
        public void update4(Integer id, String name, H.Response resp) {
            doUpdate(id, name);
            resp.contentType("text/plaintext");
            resp.writeText("done");
            //resp.close();
        }

        @Command("user.get")
        @GetAction
        public User get(Integer id) {
            return findById(id);
        }

        private void doUpdate(Integer id, String name) {
            User user = get(id);
            user.name = name;
            save(user);
        }
    }
}
