package com.stylefeng.guns.core.common.constant.factory;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.stylefeng.guns.core.common.constant.cache.Cache;
import com.stylefeng.guns.core.common.constant.cache.CacheKey;
import com.stylefeng.guns.core.common.constant.state.ManagerStatus;
import com.stylefeng.guns.core.common.constant.state.MenuStatus;
import com.stylefeng.guns.modular.system.dao.*;
import com.stylefeng.guns.modular.system.model.*;
import com.stylefeng.guns.core.log.LogObjectHolder;
import com.stylefeng.guns.core.support.StrKit;
import com.stylefeng.guns.core.util.Convert;
import com.stylefeng.guns.core.util.SpringContextHolder;
import com.stylefeng.guns.core.util.ToolUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 常量的生产工厂
 *
 * @author fengshuonan
 * @date 2017年2月13日 下午10:55:21
 */
@Component
@DependsOn("springContextHolder")
public class ConstantFactory implements IConstantFactory {

    private ClubMapper clubMapper = SpringContextHolder.getBean(ClubMapper.class);
    private ClubCoachMapper clubCoachMapper = SpringContextHolder.getBean(ClubCoachMapper.class);
    private StaffSpecialMapper staffSpecialMapper = SpringContextHolder.getBean(StaffSpecialMapper.class);
    private UserCommonMapper userCommonMapper = SpringContextHolder.getBean(UserCommonMapper.class);
    private UserClubMapper userClubMapper = SpringContextHolder.getBean(UserClubMapper.class);
    private VipUserMapper vipUserMapper = SpringContextHolder.getBean(VipUserMapper.class);
    private ClubRingMapper clubRingMapper = SpringContextHolder.getBean(ClubRingMapper.class);
    private CardPtraincardMapper cardPtraincardMapper = SpringContextHolder.getBean(CardPtraincardMapper.class);
    
    private RoleMapper roleMapper = SpringContextHolder.getBean(RoleMapper.class);
    private DeptMapper deptMapper = SpringContextHolder.getBean(DeptMapper.class);
    private DictMapper dictMapper = SpringContextHolder.getBean(DictMapper.class);
    private MenuMapper menuMapper = SpringContextHolder.getBean(MenuMapper.class);
    private ClubMenuMapper clubMenuMapper = SpringContextHolder.getBean(ClubMenuMapper.class);
    private NoticeMapper noticeMapper = SpringContextHolder.getBean(NoticeMapper.class);

    public static IConstantFactory me() {
        return SpringContextHolder.getBean("constantFactory");
    }

    /**
     * 根据字典名称和字典中的值获取对应的名称
     */
    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + "getDictsByName" + "'+#name+#val")
    public String getDictsByName(String name, Integer val) {
        Dict temp = new Dict();
        temp.setName(name);
        Dict dict = dictMapper.selectOne(temp);
        if (dict == null) {
            return "";
        } else {
            Wrapper<Dict> wrapper = new EntityWrapper<>();
            wrapper = wrapper.eq("pid", dict.getId());
            List<Dict> dicts = dictMapper.selectList(wrapper);
            for (Dict item : dicts) {
                if (item.getNum() != null && item.getNum().equals(val)) {
                    return item.getName();
                }
            }
            return "";
        }
    }
    
    /**
     * 获取字典名称
     */
    @Override
    public String getDictName(Integer dictId) {
        if (ToolUtil.isEmpty(dictId)) {
            return "";
        } else {
            Dict dict = dictMapper.selectById(dictId);
            if (dict == null) {
                return "";
            } else {
                return dict.getName();
            }
        }
    }
    
    /**
     * 查询字典（根据字典父id查询子项列表）
     */
    @Override
    public List<Dict> findInDict(Integer id) {
        if (ToolUtil.isEmpty(id)) {
            return null;
        } else {
            EntityWrapper<Dict> wrapper = new EntityWrapper<>();
            List<Dict> dicts = dictMapper.selectList(wrapper.eq("pid", id));
            if (dicts == null || dicts.size() == 0) {
                return null;
            } else {
                return dicts;
            }
        }
    }

    /**
     * 获取被缓存的对象(用户删除业务)
     */
    @Override
    public String getCacheObject(String para) {
        return LogObjectHolder.me().get().toString();
    }
    

    /**
     * 通过角色ids获取角色名称
     */
    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.ROLES_NAME + "'+#roleIds")
    public String getRoleName(String roleIds) {
        Integer[] roles = Convert.toIntArray(roleIds);
        StringBuilder sb = new StringBuilder();
        for (int role : roles) {
            Role roleObj = roleMapper.selectById(role);
            if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getName())) {
                sb.append(roleObj.getName()).append(",");
            }
        }
        return StrKit.removeSuffix(sb.toString(), ",");
    }

    /**
     * 通过角色id获取角色名称
     */
    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.SINGLE_ROLE_NAME + "'+#roleId")
    public String getSingleRoleName(Integer roleId) {
        if (0 == roleId) {
            return "--";
        }
        Role roleObj = roleMapper.selectById(roleId);
        if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getName())) {
            return roleObj.getName();
        }
        return "";
    }

    /**
     * 通过角色id获取角色英文名称
     */
    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.SINGLE_ROLE_TIP + "'+#roleId")
    public String getSingleRoleTip(Integer roleId) {
        if (0 == roleId) {
            return "--";
        }
        Role roleObj = roleMapper.selectById(roleId);
        if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getName())) {
            return roleObj.getTips();
        }
        return "";
    }

    /**
     * 获取部门名称
     */
    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.DEPT_NAME + "'+#deptId")
    public String getDeptName(Integer deptId) {
        Dept dept = deptMapper.selectById(deptId);
        if (ToolUtil.isNotEmpty(dept) && ToolUtil.isNotEmpty(dept.getFullname())) {
            return dept.getFullname();
        }
        return "";
    }

    /**
     * 获取菜单的名称们(多个)
     */
    @Override
    public String getMenuNames(String menuIds) {
        Integer[] menus = Convert.toIntArray(menuIds);
        StringBuilder sb = new StringBuilder();
        for (int menu : menus) {
            Menu menuObj = menuMapper.selectById(menu);
            if (ToolUtil.isNotEmpty(menuObj) && ToolUtil.isNotEmpty(menuObj.getName())) {
                sb.append(menuObj.getName()).append(",");
            }
        }
        return StrKit.removeSuffix(sb.toString(), ",");
    }

    /**
     * 获取菜单名称
     */
    @Override
    public String getMenuName(Long menuId) {
        if (ToolUtil.isEmpty(menuId)) {
            return "";
        } else {
            Menu menu = menuMapper.selectById(menuId);
            if (menu == null) {
                return "";
            } else {
                return menu.getName();
            }
        }
    }

    /**
     * 获取菜单名称通过编号
     */
    @Override
    public String getMenuNameByCode(String code) {
        if (ToolUtil.isEmpty(code)) {
            return "";
        } else {
            Menu param = new Menu();
            param.setCode(code);
            Menu menu = menuMapper.selectOne(param);
            if (menu == null) {
                return "";
            } else {
                return menu.getName();
            }
        }
    }

    

    /**
     * 获取通知标题
     */
    @Override
    public String getNoticeTitle(Integer dictId) {
        if (ToolUtil.isEmpty(dictId)) {
            return "";
        } else {
            Notice notice = noticeMapper.selectById(dictId);
            if (notice == null) {
                return "";
            } else {
                return notice.getTitle();
            }
        }
    }

    /**
     * 获取性别名称
     */
    @Override
    public String getSexName(Integer sex) {
        return getDictsByName("性别", sex);
    }

    /**
     * 获取用户登录状态
     */
    @Override
    public String getStatusName(Integer status) {
        return ManagerStatus.valueOf(status);
    }

    /**
     * 获取菜单状态
     */
    @Override
    public String getMenuStatusName(Integer status) {
        return MenuStatus.valueOf(status);
    }

    

    /**
     * 获取子部门id
     */
    @Override
    public List<Integer> getSubDeptId(Integer deptid) {
        Wrapper<Dept> wrapper = new EntityWrapper<>();
        wrapper = wrapper.like("pids", "%[" + deptid + "]%");
        List<Dept> depts = this.deptMapper.selectList(wrapper);

        ArrayList<Integer> deptids = new ArrayList<>();

        if(depts != null && depts.size() > 0){
            for (Dept dept : depts) {
                deptids.add(dept.getId());
            }
        }

        return deptids;
    }

    /**
     * 获取所有父部门id
     */
    @Override
    public List<Integer> getParentDeptIds(Integer deptid) {
        Dept dept = deptMapper.selectById(deptid);
        String pids = dept.getPids();
        String[] split = pids.split(",");
        ArrayList<Integer> parentDeptIds = new ArrayList<>();
        for (String s : split) {
            parentDeptIds.add(Integer.valueOf(StrKit.removeSuffix(StrKit.removePrefix(s, "["), "]")));
        }
        return parentDeptIds;
    }

    /**
     * 根据俱乐部id获取俱乐部名称
     *
     * @author guiyj007
     * @Date 2017/5/9 23:41
     */
    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + "getClubNameById" + "'+#clubId")
    public String getClubNameById(Integer clubId) {
        Club club = clubMapper.selectById(clubId);
        if (club != null) {
            return club.getName();
        } else {
            return "--";
        }
    }
    
    /**
     * 获取菜单名称通过编号
     */    @Override
    public String getClubMenuNameByCode(String code) {
        if (ToolUtil.isEmpty(code)) {
            return "";
        } else {
        	ClubMenu param = new ClubMenu();
            param.setCode(code);
            ClubMenu menu = clubMenuMapper.selectOne(param);
            if (menu == null) {
                return "";
            } else {
                return menu.getName();
            }
        }
    }

	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getCoachNameById" + "'+#bindingId")
	public String getCoachNameById(int bindingId) {
		ClubCoach clubCoach = clubCoachMapper.selectById(bindingId);
        if (clubCoach != null) {
            return clubCoach.getRealname();
        } else {
            return "--";
        }
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getCoachByUserId" + "'+#bindingId")
	public ClubCoach getCoachByUserId(int bindingId) {
		ClubCoach entity = new ClubCoach();
		entity.setUserId(bindingId);
		return clubCoachMapper.selectOne(entity);
	}

	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getStaffSpecialNameById" + "'+#bindingId")
	public String getStaffSpecialNameById(int bindingId) {
		// TODO Auto-generated method stub
		StaffSpecial staffSpecial = staffSpecialMapper.selectById(bindingId);
        if (staffSpecial != null) {
            return staffSpecial.getRealname();
        } else {
            return "--";
        }
	}

	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getUserCommonById" + "'+#bindingId")
	public UserCommon getUserCommonById(int bindingId) {
		return userCommonMapper.selectById(bindingId);
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getUserCommonNicknameById" + "'+#bindingId")
	public String getUserCommonNicknameById(int bindingId) {
		UserCommon userCommon = userCommonMapper.selectById(bindingId);
        if (userCommon != null) {
            return userCommon.getNickname();
        } else {
            return "--";
        }
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getUserClubById" + "'+#bindingId")
	public UserClub getUserClubById(int clubUserId) {
		return userClubMapper.selectById(clubUserId);
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getPtrainCardBasic" + "'+#cardId")
	public HashMap<String, Object> getPtrainCardBasic(int cardId) {
		CardPtraincard cardPtraincard = cardPtraincardMapper.selectById(cardId);
		return (HashMap<String, Object>) new MapItemFactory().composeMap(
			"title", cardPtraincard.getTitle(),
			"actualMoney", cardPtraincard.getActualMoney(),
			"unitPrice", cardPtraincard.getUnitPrice()
		);
	}
	
	@Override
	public Integer getClubUserCommonId(int clubUserId) {
		UserClub clubUser = this.getUserClubById(clubUserId); 
		if (null ==clubUser) {
			return 0;
		}
		return clubUser.getUserId();
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getVipRealnameById" + "'+#bindingId")
	public String getVipRealnameById(int bindingId) {
		VipUser vipUser = vipUserMapper.selectById(bindingId);
        if (vipUser != null) {
            return vipUser.getRealname();
        } else {
            return "--";
        }
	}
	
	@Override
//	@Cacheable(value = Cache.CONSTANT, key = "'" + "getVipIdByClubUserid" + "'+#userId+#clubId")
	public Integer getVipIdByClubUserid(int userId, int clubId) {
		Wrapper<VipUser> wrapper = new EntityWrapper<>();
        wrapper = wrapper.eq("user_id", userId);
        wrapper = wrapper.eq("club_id", clubId);
        
		List<VipUser> listVipUser = vipUserMapper.selectList(wrapper);
        if (listVipUser.size() > 0) {
        	VipUser vipUser = listVipUser.get(0);
            return vipUser.getId();
        } else {
            return 0;
        }
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getVipInfoById" + "'+#id")
	public VipUser getVipInfoById(int id) {
		VipUser vipUser = vipUserMapper.selectById(id);
        return vipUser;
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + "getUseridByVipid" + "'+#id")
	public Integer getUseridByVipid(int vipId) {
		VipUser vipUser = getVipInfoById(vipId);
		if (vipUser == null) {
			return 0;
		}
        return vipUser.getUserId();
	}
	
	@Override
	public ClubRing getClubRing(Integer clubId, String ringNum) {
		ClubRing clubRing = new ClubRing();
		clubRing.setClubId(clubId);
		clubRing.setRingNum(ringNum);
		clubRing = clubRingMapper.selectOne(clubRing);
        return clubRing;
	}
}
