/**
 * 
 */
package com.zhisland.data.social.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.zhisland.data.social.dtos.UserRelationDto;

/**
 * @author muzongyan
 *
 */
public interface UserRelationMapper {

    @Insert("insert into tb_user_1_degree(from_uid, to_uid) values (#{userA}, #{userB}), (#{userB}, #{userA})")
    public void insert(@Param("userA") int userA, @Param("userB") int userB);

    @Delete("delete from tb_user_1_degree where (from_uid = #{userA} and to_uid = #{userB}) or (from_uid = #{userB} and to_uid = #{userA})")
    public void delete(@Param("userA") int userA, @Param("userB") int userB);

    @Delete("TRUNCATE TABLE tb_user_1_degree")
    public void truncate();

    @Select("select from_uid as fromUid, to_uid as toUid from tb_user_1_degree limit #{offset}, #{count}")
    public List<UserRelationDto> getRels(@Param("offset") int offset, @Param("count") int count);

}
