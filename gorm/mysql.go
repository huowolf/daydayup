package gorm

import (
	"fmt"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
	"time"
)

var (
	DB *gorm.DB
)

func InitDB()  {
	dsn := fmt.Sprintf("%s:%s@tcp(%s:%d)/%s?charset=utf8mb4&parseTime=True&loc=Local",
		"root", "root", "localhost", 3306, "gorm")
	db, err := gorm.Open(mysql.Open(dsn))

	if err != nil {
		return
	}
	DB = db

	sqlDB, err := db.DB()

	// SetMaxIdleConns 设置空闲连接池中连接的最大数量
	sqlDB.SetMaxIdleConns(10)

	// SetMaxOpenConns 设置打开数据库连接的最大数量。
	sqlDB.SetMaxOpenConns(100)

	// SetConnMaxLifetime 设置了连接可复用的最大时间。
	sqlDB.SetConnMaxLifetime(time.Hour)
}