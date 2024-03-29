package com.nowcoder.dao;

import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

//    SELECT	*, count( id ) AS id FROM  ( SELECT * FROM message where from_id = userId or to_id = userId ORDER BY created_date DESC LIMIT 0, 100 ) tt GROUP BY conversation_id ORDER BY created_date DESC;
    @Select({"select ", INSERT_FIELDS, ", count(id) as id from ( select ", SELECT_FIELDS, " from ", TABLE_NAME, "  where from_id=#{userId} or to_id=#{userId} ORDER BY created_date DESC LIMIT 0, 100 ) tt GROUP BY conversation_id ORDER BY created_date DESC limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    @Select({"select count(id)  from ", TABLE_NAME, " where has_read=0 and to_id = #{userId} and conversation_id = #{conversationId}"})
    int getUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    @Update({"update ", TABLE_NAME, " set has_read = 1 where conversation_id = #{conversationId}"})
    int updateHasRead(@Param("conversationId") String conversationId);

}
