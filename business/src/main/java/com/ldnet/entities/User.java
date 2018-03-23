package com.ldnet.entities;

import java.io.Serializable;

//当前登录用户的信息
public class User implements Serializable {

	private static final long serialVersionUID = 6012222825670374690L;

	// 构造函数
	public User() {
		this.UserId = "";
		this.UserName = "";
		this.UserPhone = "";
		this.UserPassword = "";
		this.UserThumbnail = "";
		this.PropertyId = "";
		this.PropertyName = "";
		this.PropertyThumbnail = "";
		this.PropertyPhone="";
		this.HouseId = "";
		this.HouseName = "";
		this.CommunityId = "";
		this.CommuntiyName = "";
		this.CommuntiyAddress = "";
		this.CommuntiyLatitude = "";
		this.CommuntiyLongitude = "";
		this.CommuntiyCityId="";
        this.CZAID="";
        this.CZAUserId="";
        this.CZAToken="";
        this.HasRoom = 0;
    }

	// 用户ID
	public String UserId;

	// 用户名称
	public String UserName;

	// 用户电话--用户账号
	public String UserPhone;

	// 用户密码
	public String UserPassword;

	// 用户图像
	public String UserThumbnail;

	// 物业ID
	public String PropertyId;

	// 物业名称

    public String getCZAID() {
        return CZAID;
    }

    public void setCZAID(String CZAID) {
        this.CZAID = CZAID;
    }

    public String getCZAUserId() {
        return CZAUserId;
    }

    public void setCZAUserId(String CZAUserId) {
        this.CZAUserId = CZAUserId;
    }

    public String PropertyName;

	// 物业图像
	public String PropertyThumbnail;

    public String getCZAToken() {
        return CZAToken;
    }

    public void setCZAToken(String CZAToken) {
        this.CZAToken = CZAToken;
    }

    // 物业电话
	public String PropertyPhone;

	// 房屋Id
	public String HouseId;

	// 房号缩写
	public String HouseName;

	// 小区ID
	public String CommunityId;

	// 小区名称
	public String CommuntiyName;

	// 小区地址
	public String CommuntiyAddress;

	// 小区地理-纬度
	public String CommuntiyLatitude;

	// 小区地理-经度
	public String CommuntiyLongitude;

	//小区所在城市的ID
	public String CommuntiyCityId;
    //充智安
    public String CZAID;
    public String CZAUserId;
    public String CZAToken;

    //是否有业主身份的房子
    public int HasRoom; //1 有房子，0 没有

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public String getUserThumbnail() {
        return UserThumbnail;
    }

    public void setUserThumbnail(String userThumbnail) {
        UserThumbnail = userThumbnail;
    }

    public String getPropertyId() {
        return PropertyId;
    }

    public void setPropertyId(String propertyId) {
        PropertyId = propertyId;
    }

    public String getPropertyName() {
        return PropertyName;
    }

    public void setPropertyName(String propertyName) {
        PropertyName = propertyName;
    }

    public String getPropertyThumbnail() {
        return PropertyThumbnail;
    }

    public void setPropertyThumbnail(String propertyThumbnail) {
        PropertyThumbnail = propertyThumbnail;
    }

    public String getPropertyPhone() {
        return PropertyPhone;
    }

    public void setPropertyPhone(String propertyPhone) {
        PropertyPhone = propertyPhone;
    }

    public String getHouseId() {
        return HouseId;
    }

    public void setHouseId(String houseId) {
        HouseId = houseId;
    }

    public String getHouseName() {
        return HouseName;
    }

    public void setHouseName(String houseName) {
        HouseName = houseName;
    }

    public String getCommunityId() {
        return CommunityId;
    }

    public void setCommunityId(String communityId) {
        CommunityId = communityId;
    }

    public String getCommuntiyName() {
        return CommuntiyName;
    }

    public void setCommuntiyName(String communtiyName) {
        CommuntiyName = communtiyName;
    }

    public String getCommuntiyAddress() {
        return CommuntiyAddress;
    }

    public void setCommuntiyAddress(String communtiyAddress) {
        CommuntiyAddress = communtiyAddress;
    }

    public String getCommuntiyLatitude() {
        return CommuntiyLatitude;
    }

    public void setCommuntiyLatitude(String communtiyLatitude) {
        CommuntiyLatitude = communtiyLatitude;
    }

    public String getCommuntiyLongitude() {
        return CommuntiyLongitude;
    }

    public void setCommuntiyLongitude(String communtiyLongitude) {
        CommuntiyLongitude = communtiyLongitude;
    }

    public String getCommuntiyCityId() {
        return CommuntiyCityId;
    }

    public void setCommuntiyCityId(String communtiyCityId) {
        CommuntiyCityId = communtiyCityId;
    }
}
