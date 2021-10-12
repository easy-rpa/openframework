On Error Resume Next

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

Call workbook.Sheets(Wscript.Arguments(1)).Columns(Wscript.Arguments(2) & ":" & Wscript.Arguments(2)).Insert()

If Err.Number <> 0 Then
	workbook.Close False
	excel.Application.Quit
	WScript.Echo "Error #" & Err.Number & " " & Err.Description
	WScript.Quit Err.Number
End If

workbook.Save
workbook.Close True
excel.Application.Quit
WScript.Echo "SUCCESS"
WScript.Quit


'script call example 
'cscript "insertColumn.vbs" "C:/Users/User/git/trep-10-kyriba-users-review/src/test/java/Processed users.xlsx" "User list" "B"
