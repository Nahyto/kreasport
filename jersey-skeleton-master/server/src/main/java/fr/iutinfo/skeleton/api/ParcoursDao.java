package fr.iutinfo.skeleton.api;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.tweak.BeanMapperFactory;

public interface ParcoursDao {
    @SqlUpdate("create table parcours (id integer primary key autoincrement, name varchar(100), key varchar(100), description TEXT)")
    void createParcoursTable();

    @SqlUpdate("insert into parcours (name,key,description) values (:name, :key, :description)")
    @GetGeneratedKeys
    int insert(@BindBean() Parcours parcours);

    @SqlQuery("select * from parcours where name = :name")
    @RegisterMapperFactory(BeanMapperFactory.class)
    Parcours findByName(@Bind("name") String name);

    @SqlQuery("select * from parcours where search like :name")
    @RegisterMapperFactory(BeanMapperFactory.class)
    List<Parcours> search(@Bind("name") String name);

    @SqlUpdate("drop table if exists parcours")
    void dropParcoursTable();

    @SqlUpdate("delete from parcours where id = :id")
    void delete(@Bind("id") int id);

    @SqlQuery("select * from parcours order by id")
    @RegisterMapperFactory(BeanMapperFactory.class)
    List<Parcours> all();

	@SqlQuery("select * from parcours INNER JOIN balise ON parcours.id = balise.parcours ORDER BY parcours.id")
	@RegisterMapperFactory(BeanMapperFactory.class)
	List<Parcours> allWithBalises();

    @SqlQuery("select * from parcours where id = :id")
    @RegisterMapperFactory(BeanMapperFactory.class)
    Parcours findById(@Bind("id") int id);
    
   @SqlQuery("select * from balise where parcours = :id")
   @RegisterMapperFactory(BeanMapperFactory.class)
   List<Balise> getBalisebyId(@Bind("id") int id);
   
   @SqlUpdate("update parcours SET name = :name , key = :key, description = :description  where id = :id")
   void updateParcours(@Bind("name")String name, @Bind("key") String key,@Bind("description") String description, @Bind("id") int id);

    void close();
}
