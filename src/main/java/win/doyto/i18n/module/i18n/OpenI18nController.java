package win.doyto.i18n.module.i18n;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import win.doyto.i18n.component.view.I18nXlsxView;
import win.doyto.i18n.module.locale.ResourceLocaleQuery;
import win.doyto.i18n.module.locale.ResourceLocaleService;
import win.doyto.web.spring.RestBody;

/**
 * I18nController
 *
 * @author f0rb on 2017-03-30.
 */
@Slf4j
@Controller
@RequestMapping("/openapi")
public class OpenI18nController {
    private I18nService i18nService;

    private ResourceLocaleService resourceLocaleService;

    public OpenI18nController(I18nService i18nService, ResourceLocaleService resourceLocaleService) {
        this.i18nService = i18nService;
        this.resourceLocaleService = resourceLocaleService;
    }

    /**
     * 导出所有的标签和语言
     *
     * @param group 资源分组名称
     * @return i18n.xlsx
     */
    @RequestMapping(value = "{user}/{group}.xlsx", method = RequestMethod.GET)
    public View exportAllToExcel(Model model, @PathVariable("user") String user, @PathVariable("group") String group) {
        i18nService.checkGroup(user, group);
        List data = i18nService.query(user, group);
        model.addAttribute("data", data);
        return new I18nXlsxView();
    }

    @RequestMapping(value = "{user}/{group}/locale", method = RequestMethod.GET)
    @ResponseBody
    @RestBody
    public Object query(ResourceLocaleQuery resourceLocaleQuery, @PathVariable("user") String user, @PathVariable("group") String group) {
        resourceLocaleQuery.setGroup(group);
        return resourceLocaleService.query(resourceLocaleQuery);
    }

    @RequestMapping(value = "{user}/{group}/{locale}.json", method = RequestMethod.GET)
    @ResponseBody
    @RestBody
    public Object exportToJsonByLocale(
            @PathVariable("user") String user,
            @PathVariable("group") String group,
            @PathVariable("locale") String locale) {

        i18nService.checkGroupAndLocale(user, group, locale);
        List<Lang> langList = i18nService.query(user, group, locale);

        JSONObject root = new JSONObject();

        for (Lang lang : langList) {
            nestedJson(root, lang.getLabel().split("\\."), 0, lang.getValue());
        }

        return root;
    }

    /**
     * 处理嵌套的JSON参数
     *
     * @param parent 上级json
     * @param params 参数名
     * @param deep   递归深度
     * @param arg    待处理的参数
     */
    private static void nestedJson(JSONObject parent, String[] params, int deep, Object arg) {
        String param = params[deep];
        if (deep == params.length - 1) {
            parent.put(params[deep], arg);
            return;
        }
        if (!parent.containsKey(param)) {
            parent.put(param, new JSONObject());
        }
        nestedJson(parent.getJSONObject(param), params, deep + 1, arg);
    }

}
