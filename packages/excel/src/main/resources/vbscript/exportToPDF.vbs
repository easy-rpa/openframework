On Error Resume Next

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

Set sheetToExport = workbook.Sheets(Wscript.Arguments(1))
sheetToExport.Select
sheetToExport.ExportAsFixedFormat 0, Replace(Wscript.Arguments(2), "/", "\"), 0, 1, 0, , , 0  

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
