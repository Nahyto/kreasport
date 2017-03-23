package fr.iutinfo.skeleton.api;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.tweak.BeanMapperFactory;

import java.util.List;

public interface BaliseDAO {
    @SqlUpdate("create table balise (id integer primary key autoincrement, longitude double, latitude double, parcours int, FOREIGN KEY (parcours) REFERENCES parcours(id))")
    void createBaliseTable();

    @SqlUpdate("insert into balise (longitude,latitude,parcours) values (:longitude, :latitude, :parcours)")
    @GetGeneratedKeys
    int insert(@BindBean() Balise balise);

    @SqlQuery("select * from balise where name = :name")
    @RegisterMapperFactory(BeanMapperFactory.class)
    Balise findByName(@Bind("name") String name);

    @SqlQuery("select * from balise where search like :name")
    @RegisterMapperFactory(BeanMapperFactory.class)
    List<Balise> search(@Bind("name") String name);

    @SqlUpdate("drop table if exists balise")
    void dropBaliseTable();

    @SqlUpdate("delete from balise where id = :id")
    void delete(@Bind("id") int id);

    @SqlQuery("select * from balise order by id")
    @RegisterMapperFactory(BeanMapperFactory.class)
    List<Balise> all();

    @SqlQuery("select * from balise where id = :id")
    @RegisterMapperFactory(BeanMapperFactory.class)
    Balise findById(@Bind("id") int id);

    @SqlUpdate("update parcours SET balise = :balise where id = :id")
    void updateBalise(@Bind("id")int id, @Bind("balise") String balise);
    
    void close();
}
