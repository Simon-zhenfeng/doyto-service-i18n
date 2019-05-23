package win.doyto.i18n.module.i18n;

import lombok.*;
import win.doyto.query.core.PageQuery;
import win.doyto.query.core.QueryTable;

/**
 * I18nQuery
 *
 * @author f0rb
 * @date 2019-05-21
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@QueryTable(table = I18nMapper.GROUP_FORMAT, entityClass = I18nEntity.class)
public class I18nQuery extends PageQuery {

    private String user;

    private String group;

    private Boolean valid = true;

}