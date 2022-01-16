package gorm

import (
	"database/sql"
	"errors"
	"gorm.io/gorm"
	"log"
	"time"
)

type User struct {
	BaseModel
	Name         string
	Email        *string
	Age          uint8       `gorm:"default:18"`
	Birthday     time.Time
	MemberNumber sql.NullString
	ActivatedAt  sql.NullTime

	CreditCard CreditCard
}

// TableName 自定义表名
//func (u User) TableName() string {
//	return "user"
//}


// BeforeCreate 钩子函数
func (u *User) BeforeCreate(tx *gorm.DB) (err error) {
	log.Printf("创建用户：%s",u.Name)
	err = DB.Where("name=?", u.Name).First(&u).Error
	if !errors.Is(err,gorm.ErrRecordNotFound) {
		return errors.New("已存在同名用户！")
	}
	return nil
}



