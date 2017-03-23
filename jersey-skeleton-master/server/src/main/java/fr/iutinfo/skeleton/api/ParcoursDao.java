package fr.iutinfo.skeleton.api;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.tweak.BeanMapperFactory;

import java.util.List;

public interface ParcoursDao {
    @SqlUpdate("create table parcours (id integer primary key autoincrement, name varchar(100), key varchar(100), balise varchar(100))")
    void createParcoursTable();

    @SqlUpdate("insert into parcours (name,key) values (:name, :key)")
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

    @SqlQuery("select * from parcours where id = :id")
    @RegisterMapperFactory(BeanMapperFactory.class)
    Parcours findById(@Bind("id") int id);
    
   @SqlQuery("select * from balise where parcours = :id")
   @RegisterMapperFactory(BeanMapperFactory.class)
   List<Balise> getBalisebyId(@Bind("id") int id);

    void close();
}
