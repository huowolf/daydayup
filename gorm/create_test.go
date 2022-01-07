package gorm

import (
	"database/sql"
	"fmt"
	"testing"
	"time"
)

func before()  {
	InitDB()
}

func TestDemo1(t *testing.T) {
	fmt.Println(DB == nil)
	user := User{Name: "Jinzhu", Age: 18, Birthday: time.Now()}
	result := DB.Create(&user) // 通过数据的指针来创建

	fmt.Println(user.ID)  		// 返回插入数据的主键
	fmt.Println(result.Error)	// 返回 error
	fmt.Println(result.RowsAffected)  // 返回插入记录的条数
}

func TestDemo2(t *testing.T) {
	user := User{
		Name: "test",
		Age: 28,
		MemberNumber: sql.NullString{String: "100"},
		CreatedAt: time.Now(),
	}

	result := DB.Select("Name","Age","CreateAt").Create(&user)
	if result.Error != nil {
		fmt.Println(result.Error)
	}
}

func TestMain(m *testing.M) {
	before()
	m.Run()
}
