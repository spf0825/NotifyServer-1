import org.codehaus.groovy.grails.web.converters.marshaller.json.EasyuiDomainClassMarshaller
import grails.converters.JSON;
import com.basic.core.*;
import com.bjrxht.notify.*;
class BootStrap {

    def init = { servletContext ->
        JSON.registerObjectMarshaller(new EasyuiDomainClassMarshaller(false,true,10));
        createRole()
        createDefaultRoles()
        createDefaultUsers()
        createRequestMap()
        createBank();
        createPhone();
        createAccount();
        createStragegy();
    }
    def destroy = {
    }
    private void createBank(){
        def banks=['鹤壁银行','B银行'];
        banks.each{
            if (!Bank.findByName(it)) {
                new Bank(name:it).save()
            }
        }
    }
    private void createPhone(){
        def phones=['13910818313','13901351039','13917309395'];
        phones.each{
            if (!Phone.findByNumber(it)) {
                def phone=new Phone(number:it).save(flush: true);
            }
        }
    }
    private void createAccount(){
        def accounts=[['A000002','鹤壁银行','13910818313'],['A000001','鹤壁银行','13901351039'],['C000001','鹤壁银行','13901351039']];
        accounts.each{
            if (!Account.findByNumber(it[0])) {
                def account=new Account(number:it[0],bank: Bank.findByName(it[1]),phone: Phone.findByNumber(it[2])).save(flush: true);
            }
        }
    }
    private void createStragegy(){
        def stragetys=[['A000001','between',0,99999999],['A000002','between',300,99999999],['C000001','between',300,99999999]];
        stragetys.each{
            def account=Account.findByNumber(it[0]);
            if (!Strategy.findByAccount(account)) {
                new Strategy(account:account,type:it[1],min:it[2],max:it[3]).save(flush: true);
            }
        }
    }
    private createRole(){
        def roles=['IS_AUTHENTICATED_ANONYMOUSLY','IS_AUTHENTICATED_FULLY','IS_AUTHENTICATED_REMEMBERED'];
        roles.each{
            if (!BaseRole.findByAuthority(it)) {
                new BaseRole(authority:it,description:it).save()
            }
        }
    }
    private createDefaultRoles() {
        if (!BaseRole.findByAuthority('ROLE_ADMIN'))
            new BaseRole(authority:'ROLE_ADMIN',description:"超级管理员").save()
        if (!BaseRole.findByAuthority('ROLE_USER'))
            new BaseRole(authority:'ROLE_USER',description:"普通用户").save()
        if (!BaseRole.findByAuthority('ROLE_INCASE'))
            new BaseRole(authority:'ROLE_INCASE',description:"柜面用户").save()
    }

    private def createDefaultUsers() {
        def admin = BaseUser.findByUsername('admin')
        if (!admin) {
            admin = new BaseUser(username:'admin',password: 'admin',enabled:true,accountExpired:false,accountLocked:false,passwordExpired:false)
            admin.save()
        }
        def user = BaseUser.findByUsername('hbBank')
        if (user == null) {
            user = new BaseUser(username:'hbBank',password: 'defaultPasswd',enabled:true,accountExpired:false,accountLocked:false,passwordExpired:false)
            user.save()
        }
        def test = BaseUser.findByUsername('test')
        if(test == null){
            test = new BaseUser(username: 'test',password: 'test',enabled:true,accountExpired:false,accountLocked:false,passwordExpired:false);
            test.save();
        }
        def incase = BaseUser.findByUsername('incase')
        if(incase == null){
            incase = new BaseUser(username: 'incase',password: 'incase',enabled:true,accountExpired:false,accountLocked:false,passwordExpired:false);
            incase.save();
        }
        new BaseUserBaseRole(baseUser:admin,baseRole:BaseRole.findByAuthority('ROLE_ADMIN')).save();
        new BaseUserBaseRole(baseUser:user,baseRole:BaseRole.findByAuthority('ROLE_USER')).save();
        new BaseUserBaseRole(baseUser:test,baseRole:BaseRole.findByAuthority('ROLE_USER')).save();
        new BaseUserBaseRole(baseUser:incase,baseRole:BaseRole.findByAuthority('ROLE_INCASE')).save();

    }

    private def createRequestMap() {

        new Requestmap(url: '/js/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/imgFile/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/ocr/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/images/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/plugins/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/css/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/login/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/logout/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/favicon.ico', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/images/favicon.ico', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/register/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/j_spring_security_check', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/messagebroker/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/abstractS2Ui/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/easyui/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclClass/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclEntry/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclObjectIdentity/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/aclSid/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/persistentLogin/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/register/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/registrationCode/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/role/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/user/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/securityInfo/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        //domain Controller
        new Requestmap(url: '/**/inList**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/requestmap/**', configAttribute: 'ROLE_ADMIN').save()
        new Requestmap(url: '/baseUser/**', configAttribute: 'ROLE_ADMIN').save()
        new Requestmap(url: '/baseRole/**', configAttribute: 'ROLE_ADMIN').save()

        //
        new Requestmap(url: '/message/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/info/pushInfo/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/info/feedBack/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()

    }
}
