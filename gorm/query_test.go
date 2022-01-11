package gorm

import (
	"errors"
	"fmt"
	"gorm.io/gorm"
	"testing"
	"time"
)

func TestQuery1(t *testing.T) {
	err := DB.Where("name=?", "test1").First(&User{}).Error
	if errors.Is(err,gorm.ErrRecordNotFound){
		fmt.Println("记录不存在！")
	}
}

func TestQuery2(t *testing.T) {
	var user1 User

	//获取第一条记录，根据主键升序排列
	DB.First(&user1)
	fmt.Println(user1)

	//没有指定排序字段
	var user2 User
	DB.Take(&user2)
	fmt.Println(user2)

	//获取最后一条记录，主键降序
	var user3 User
	DB.Last(&user3)
	fmt.Println(user3)
}

func TestQuery3(t *testing.T) {
	var user User
	DB.First(&user,10)
	fmt.Println(user)
}

func TestQuery4(t *testing.T) {
	var users []User
	DB.Find(&users,[]int{1,2,3})
	fmt.Println(len(users))
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery5(t *testing.T) {
	var users []User
	DB.Where("name <> ?", "jinzhu").Find(&users)
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery6(t *testing.T) {
	var users []User
	DB.Where("name IN ?",[]string{"jinzhu", "jinzhu 2"}).Find(&users)
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery7(t *testing.T) {
	var users []User
	DB.Where("name LIKE ?", "%jin%").Find(&users)
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery8(t *testing.T) {
	var users []User
	DB.Where("name = ? AND age >= ?", "jinzhu", "22").Find(&users)
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery9(t *testing.T) {
	var users []User
	lastWeek, _ := time.ParseInLocation("2006-01-02 15:04:05", "2022-01-09 22:01:02",time.Local)

	DB.Where("updated_at > ?", lastWeek).Find(&users)
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery10(t *testing.T) {
	var users []User
	DB.Where("age BETWEEN ? AND ?", 10, 20).Find(&users)
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery11(t *testing.T) {
	var user User
	DB.Where(&User{Name: "jinzhu",Age: 20}).First(user)
	fmt.Println(user)
}

func TestQuery12(t *testing.T) {
	var users []User
	DB.Where(map[string]interface{}{"name": "test", "age": 28}).Find(&users)
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery13(t *testing.T) {
	var users []User
	DB.Where([]int64{10, 11, 12}).Find(&users)
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery14(t *testing.T) {
	var users []User
	DB.Where(&User{Name: "jinzhu"}, "Age").Find(&users)
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery15(t *testing.T) {
	var users []User
	DB.Find(&users, "name=?", "jinzhu")
	for _, user := range users {
		fmt.Println(user)
	}
}

func TestQuery16(t *testing.T) {
	var user User
	DB.Not("name = ?", "jinzhu").First(&user)
		fmt.Println(user)
}