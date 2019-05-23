package win.doyto.i18n.module.group;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import win.doyto.auth.annotation.CurrentUser;
import win.doyto.common.web.ErrorCode;
import win.doyto.common.web.JsonBody;
import win.doyto.i18n.common.I18nErrorCode;
import win.doyto.query.core.PageList;

import java.util.Date;
import javax.validation.Valid;

/**
 * GroupController
 *
 * @author f0rb on 2017-03-29.
 */
@Slf4j
@JsonBody
@RestController
@RequestMapping({"/api/resource-group", "/api/group"})
@PreAuthorize("hasAnyRole('i18n')")
class GroupController implements GroupApi {

    private GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public PageList<GroupResponse> page(@CurrentUser String owner, GroupQuery groupQuery) {
        groupQuery.setOwner(owner);
        return page(groupQuery);
    }

    public PageList<GroupResponse> page(GroupQuery groupQuery) {
        return groupService.page(groupQuery, GroupResponse::build);
    }

    @PostMapping("update/label")
    public void updateLabel(@RequestBody @Valid UpdateGroupLabelRequest group) {
        groupService.updateLabel(group.getId(), group.getLabel());
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@CurrentUser String owner, @PathVariable("id") Integer id) {
        GroupEntity groupEntity = groupService.get(id);
        ErrorCode.assertNotNull(groupEntity, I18nErrorCode.RECORD_NOT_FOUND);
        groupEntity.setOwner(owner);
        groupEntity.setDeleted(true);
        groupService.update(groupEntity);
    }

    public void insertGroup(String owner, String name, String label) {
        GroupEntity groupEntity;
        groupEntity = new GroupEntity();
        groupEntity.setOwner(owner);
        groupEntity.setName(name);
        groupEntity.setLabel(label);
        groupEntity.setValid(true);
        groupEntity.setDeleted(false);
        groupEntity.setCreateTime(new Date());
        groupEntity.setUpdateTime(new Date());
        groupService.create(groupEntity);
    }

    @Override
    public GroupResponse get(Integer groupId) {
        return GroupResponse.build(groupService.get(groupId));
    }

    @Override
    public GroupResponse getGroup(String username, String group) {
        GroupEntity group1 = groupService.getByName(username, group);
        ErrorCode.assertNotNull(group1, I18nErrorCode.RECORD_NOT_FOUND);
        return GroupResponse.build(group1);
    }

    @Data
    public static class UpdateGroupLabelRequest {
        private Integer id;
        private String label;
    }
}