package gorm

type CreditCard struct {
	BaseModel
	Number   string
	UserID   uint
}