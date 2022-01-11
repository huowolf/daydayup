package gorm

import "time"

type BaseModel struct {
	ID        uint
	CreatedAt time.Time
	UpdatedAt time.Time
}
