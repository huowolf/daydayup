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
	user := User{Name: "Jinzhu444", Age: 18, Birthday: time.Now()}
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
	}

	result := DB.Select("Name","Age","CreateAt").Create(&user)
	if result.Error != nil {
		fmt.Println(result.Error)
	}
}

func TestDemo3(t *testing.T) {
	var users = []User{{Name: "jinzhu1"}, {Name: "jinzhu2"}, {Name: "jinzhu3"}}
	DB.Create(&users)

	for _, user := range users {
		fmt.Println(user.ID) // 1,2,3
	}
}

// 注意：CreatedAt,UpdatedAt不会被填充，association不会被调用
func TestDemo4(t *testing.T) {
	m := []map[string]interface{}{
		{"Name": "jinzhu_1", "Age": 18},
		{"Name": "jinzhu_2", "Age": 20,},
	}
	DB.Model(&User{}).Create(m)
}

func TestDemo5(t *testing.T) {
	user := User{
		Name: "zhangsan222",
		CreditCard: CreditCard{Number: "411111111111"},
	}

	DB.Create(&user)
}

func TestMain(m *testing.M) {
	before()
	m.Run()
}
