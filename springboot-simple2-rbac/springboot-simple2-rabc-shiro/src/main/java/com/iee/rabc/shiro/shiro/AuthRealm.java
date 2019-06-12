package com.iee.rabc.shiro.shiro;

import com.iee.rabc.shiro.entity.Module;
import com.iee.rabc.shiro.entity.Role;
import com.iee.rabc.shiro.entity.User;
import com.iee.rabc.shiro.service.RoleService;
import com.iee.rabc.shiro.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @Description AuthRealm完成根据用户名去数据库的查询,并且将用户信息放入shiro中, 供CredentialsMatcher类使用
 * @Author longxiaonan@163.com
 * @Date 22:07 2019/6/11 0011
 **/
public class AuthRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    //认证.登录
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken utoken=(UsernamePasswordToken) token;//获取用户输入的token
        String username = utoken.getUsername();
        Optional<User> user = userService.findUserByUserName(username);
        if(user.isPresent()){
            //放入shiro.调用CredentialsMatcher检验密码
            return new SimpleAuthenticationInfo(user, user.get().getPassword(),super.getName());
        }
        throw new AuthenticationException("用户名或者密码错误！");

    }
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        User user=(User) principal.fromRealm(this.getClass().getName()).iterator().next();//获取session中的用户
        List<String> permissions = roleService.getModules(user);
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
//        info.addRoles();
        info.addStringPermissions(permissions);//将权限放入shiro中.
        return info;
    }

}
