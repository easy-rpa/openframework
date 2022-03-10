On Error Resume Next

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

workbook.Sheets(Wscript.Arguments(1)).Range(Wscript.Arguments(3)).AutoFilter Wscript.Arguments(4), Wscript.Arguments(2) 

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


'cscript "C:/Users/andreid/git/spotify-automation-core/src/main/resources/scripts/filter.vbs" "C:/Users/andreid/AppData/Local/Temp/User Report.xlsx" "Sheet0" "Active" "F1:F183" "1"
