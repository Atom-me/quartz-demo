package com.atom.quartz.respository;

import com.atom.quartz.model.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Atom
 */
@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {

}
