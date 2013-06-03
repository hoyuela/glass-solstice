Given /^I switch to (card|bank)$/ do |which|
		
	x = query("imageview marked:'#{"cardBankIcon"}'")[0]["rect"]["center_x"]
	y = query("imageview marked:'#{"cardBankIcon"}'")[0]["rect"]["center_y"]
	performAction("touch_coordinate", x, y)

	if which == 'card'
		performAction('click_on_view_by_id', 'card_gray_icon')
	else
		performAction('click_on_view_by_id', 'bank_gray_icon')
	end
end
