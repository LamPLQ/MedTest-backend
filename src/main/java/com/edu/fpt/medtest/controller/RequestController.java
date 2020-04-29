package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.*;
import com.edu.fpt.medtest.model.*;
import com.edu.fpt.medtest.repository.*;
import com.edu.fpt.medtest.service.NotificationService;
import com.edu.fpt.medtest.service.Request.RequestHistoryService;
import com.edu.fpt.medtest.service.Request.RequestService;
import com.edu.fpt.medtest.service.Request.RequestTestService;
import com.edu.fpt.medtest.service.Request.ResultService;
import com.edu.fpt.medtest.service.Tests.TestTypeService;
import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.utils.GetRandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestHistoryService requestHistoryService;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestHistoryRepository requestHistoryRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private RequestTestService requestTestService;

    @Autowired
    private RequestTestRepository requestTestRepository;

    @Autowired
    private ResultService resultService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RequestModelRepository requestModelRepository;

    @Autowired
    private TestTypeService testTypeService;

    @PostMapping("/create")
    public ResponseEntity<?> createNewRequest(@RequestBody RequestModelInput requestModelInput) {
        try {
            if (requestModelInput.getUserID() == 0 || requestModelInput.getMeetingTime().toString().isEmpty() || requestModelInput.getAddress().isEmpty() || requestModelInput.getTownCode().isEmpty()
                    || requestModelInput.getDistrictCode().isEmpty() || requestModelInput.getSelectedTest().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Thông tin để tạo yêu cầu xét nghiệm mới chưa đủ"), HttpStatus.OK);
            }
            //save request model
            RequestModel requestModel = new RequestModel();
            requestModel.setAddress(requestModelInput.getAddress());
            requestModel.setMeetingTime(requestModelInput.getMeetingTime());
            requestModel.setDistrictCode(requestModelInput.getDistrictCode());
            requestModel.setTownCode(requestModelInput.getTownCode());
            requestModel.setUserID(requestModelInput.getUserID());
            requestModelRepository.save(requestModel);
            ///===================
            Request request = new Request();
            String requestID;
            do {
                requestID = GetRandomString.getAlphaNumericStringUpper(6);
            } while (requestRepository.existsByRequestID(requestID));
            request.setRequestID(requestID);
            request.setUserID(requestModel.getUserID());
            request.setMeetingTime(requestModel.getMeetingTime());
            request.setCreatedTime(requestModel.getCreatedTime());
            request.setAddress(requestModel.getAddress());
            request.setTownCode(requestModel.getTownCode());
            request.setDistrictCode(requestModel.getDistrictCode());
            //parse (String)testID into (Integer)testID and check if test available
            List<String> selectedTests = requestModelInput.getSelectedTest();
            int selectedTestID;
            List<Integer> listSelectedTestID = new ArrayList<>();
            if (selectedTests.size() == 0) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy danh sách yêu cầu xét nghiệm!"), HttpStatus.OK);
            }
            for (String selectedTest : selectedTests) {
                selectedTestID = Integer.parseInt(selectedTest);
                boolean existedTest = testRepository.existsByTestID(selectedTestID);
                if (existedTest == true) {
                    listSelectedTestID.add(selectedTestID);
                } else {
                    return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy yêu cầu xét nghiệm!"), HttpStatus.OK);
                }
            }
            //foreach available test, find and add tests by its ID
            List<RequestTest> lsRequestTest = new ArrayList<>();
            for (Integer selectingTestID : listSelectedTestID) {
                requestRepository.save(request);
                RequestTest requestTest = new RequestTest();
                requestTest.setRequestID(request.getRequestID());
                requestTest.setTestID(selectingTestID);
                lsRequestTest.add(requestTest);
            }
            //System.out.println(lsRequestTest);
            requestTestService.saveListRequestTest(lsRequestTest);
            //get list chosen test
            long testAmount = 0;
            List<String> lsChosenTest = new ArrayList<>();
            List<Integer> lsVersionOfTest = new ArrayList<>();
            for (RequestTest requestTest : lsRequestTest) {
                String chosenTest = String.valueOf(testRepository.findById(requestTest.getTestID()).get().getTestID());
                testAmount += testRepository.findById(requestTest.getTestID()).get().getPrice();
                lsChosenTest.add(chosenTest);
                lsVersionOfTest.add(testRepository.getOne(requestTest.getTestID()).getVersionID());
            }
            //System.out.println(lsChosenTest);
            System.out.println("ls Test Version " + lsVersionOfTest);
            //return detail
            DetailRequestModel detailRequestModel = new DetailRequestModel();
            detailRequestModel.setRequestID(request.getRequestID());
            detailRequestModel.setCustomerID(String.valueOf(userRepository.findById(request.getUserID()).get().getId()));
            detailRequestModel.setCustomerName(userRepository.findById(request.getUserID()).get().getName());
            detailRequestModel.setCustomerPhoneNumber(userRepository.findById(request.getUserID()).get().getPhoneNumber());
            detailRequestModel.setCustomerDOB(userRepository.findById(request.getUserID()).get().getDob());
            detailRequestModel.setRequestAddress(request.getAddress() + " " + townRepository.getOne(request.getTownCode()).getTownName()
                    + " " + districtRepository.getOne(request.getDistrictCode()).getDistrictName());
            detailRequestModel.setRequestTownID(request.getTownCode());
            detailRequestModel.setRequestTownName(townRepository.getOne(request.getTownCode()).getTownName());
            detailRequestModel.setRequestDistrictID(request.getDistrictCode());
            detailRequestModel.setRequestDistrictName(districtRepository.getOne(request.getDistrictCode()).getDistrictName());
            detailRequestModel.setRequestMeetingTime(request.getMeetingTime());
            //=====================//
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String displayCreatedTest = sdf2.format(request.getCreatedTime());
            String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
            //=====================//
            detailRequestModel.setRequestCreatedTime(createdTime);
            detailRequestModel.setRequestStatus("pending");
            detailRequestModel.setLsSelectedTest(lsChosenTest);
            detailRequestModel.setRequestAmount(String.valueOf(testAmount));
            detailRequestModel.setRequestNote("Just created!");
            detailRequestModel.setVersionOfTest(lsVersionOfTest.get(0));
            detailRequestModel.setRequestUpdatedTime(createdTime);
            requestModelRepository.delete(requestModel);
            return new ResponseEntity<>(detailRequestModel, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    /*@PostMapping("/create")
    public ResponseEntity<?> createNewRequest(@RequestBody RequestModel requestModel) {
        Request request = new Request();
        request.setUserID(requestModel.getUserID());
        request.setMeetingTime(requestModel.getMeetingTime());
        request.setCreatedDate(requestModel.getCreatedTime());
        request.setAddress(requestModel.getAddress());
        request.setTownCode(requestModel.getTownCode());
        request.setDistrictCode(requestModel.getDistrictCode());
        List<String> selectedTests = requestModel.getSelectedTest();
        int selectedTestID;
        boolean existedTest = false;
        List<Test> lsSelectedTest = new ArrayList<>();
        for (String selectedTest : selectedTests) {
            selectedTestID = Integer.parseInt(selectedTest);
            existedTest = testRepository.existsByTestID(selectedTestID);
            if (existedTest == false) {
                return new ResponseEntity<>(new ApiResponse(true, "Not found this test"), HttpStatus.NOT_FOUND);
            } else {
                Optional<Test> selectedTestObj = testRepository.findById(selectedTestID);
                lsSelectedTest.add(selectedTestObj.get());
            }
        }
        System.out.println(lsSelectedTest);
        ================infinitely list but still insert successfully to database???====================
        try {

            for (Test selectingTest : lsSelectedTest) {
                request.getTestsChosen().add(selectingTest);
                selectingTest.getRequestsChosen().add(request);
                // break;
            }
            //request.getTestsChosen().add(lsSelectedTest.get(1));
            //lsSelectedTest.get(1).getRequestsChosen().add(request);
            requestRepository.save(request);
            //requestService.saveRequest(request);
        } catch (Exception e) {
            System.out.println(e);
        }
        =========================================================
        return new ResponseEntity<>(request, HttpStatus.OK);
    }*/

    //update status of 1 request - add into request_history table
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateRequestStatus(@RequestBody RequestHistory requestHistory, @PathVariable("id") String ID) {
        try {
            Request requestPresenting = requestService.getRequest(ID);
            if (requestPresenting == null) {
                return new ResponseEntity<>(new ApiResponse(false, "Không tìm thấy mã đơn xét nghiệm " + ID), HttpStatus.OK);
            }
            List<RequestHistory> listHistoryOfCurrentRequest = requestHistoryRepository.findByRequestIDOrderByCreatedTimeDesc(ID);
            if (listHistoryOfCurrentRequest.isEmpty() || listHistoryOfCurrentRequest == null) {
                System.out.println("List empty, can update!");
                requestHistory.setRequestID(requestPresenting.getRequestID());
                requestHistoryService.save(requestHistory);
            } else {
                String inputStatus = requestHistory.getStatus();
                String currentStatus = listHistoryOfCurrentRequest.get(0).getStatus();
                if (inputStatus.equals(currentStatus)) {
                    return new ResponseEntity<>(new ApiResponse(false, "Đơn xét nghiệm đang được xử lý. Vui lòng tải lại!"), HttpStatus.OK);
                }
                String statusWillUpdate = requestHistory.getStatus();
                String lastestStatusOfRequest = listHistoryOfCurrentRequest.get(0).getStatus();
                String displayLastestStatusRequest;
                if(lastestStatusOfRequest.equals("pending")){
                    displayLastestStatusRequest = "Đang đợi y tá nhận đơn";
                }else if(lastestStatusOfRequest.equals("accepted")){
                    displayLastestStatusRequest = "Đang đợi lấy mẫu";
                }else if(lastestStatusOfRequest.equals("transporting")){
                    displayLastestStatusRequest = "Đang vận chuyển mẫu";
                }else if(lastestStatusOfRequest.equals("waitingforresult")){
                    displayLastestStatusRequest = "Đang đợi kết quả";
                }else if(lastestStatusOfRequest.equals("closed")){
                    displayLastestStatusRequest = "Đã xong";
                }else if(lastestStatusOfRequest.equals("lostsample")){
                    displayLastestStatusRequest = "Đang đợi lấy lại mẫu";
                }else if(lastestStatusOfRequest.equals("coordinatorlostsample")){
                    displayLastestStatusRequest = "Đang đợi y tá nhận đơn";
                }else if(lastestStatusOfRequest.equals("canceled")){
                    displayLastestStatusRequest = "Đã huỷ";
                }else if(lastestStatusOfRequest.equals("reaccepted")){
                    displayLastestStatusRequest = "Đã nhận đơn bị mất do điều phối viên";
                }else if(lastestStatusOfRequest.equals("retransporting")){
                    displayLastestStatusRequest = "Đang vận chuyển đơn bị mất do điều phối viên";
                }else {
                    displayLastestStatusRequest = "Đang đợi lấy lại mẫu do điều phối viên làm mất";
                }
                switch (statusWillUpdate) {
                    case "pending":
                        if (!lastestStatusOfRequest.equals("accepted")) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "accepted":
                        if (!lastestStatusOfRequest.equals("pending")) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "transporting":
                        if (!(lastestStatusOfRequest.equals("accepted") || lastestStatusOfRequest.equals("lostsample"))) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "lostsample":
                        if (!lastestStatusOfRequest.equals("transporting")) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "waitingforresult":
                        if (!(lastestStatusOfRequest.equals("transporting") || lastestStatusOfRequest.equals("retransporting"))) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "closed":
                        if (!lastestStatusOfRequest.equals("waitingforresult")) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "coordinatorlostsample":
                        if (!(lastestStatusOfRequest.equals("waitingforresult") || lastestStatusOfRequest.equals("reaccepted"))) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "reaccepted":
                        if (!lastestStatusOfRequest.equals("coordinatorlostsample")) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "retransporting":
                        if (!(lastestStatusOfRequest.equals("reaccepted") || lastestStatusOfRequest.equals("relostsample"))) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "relostsample":
                        if (!lastestStatusOfRequest.equals("retransporting")) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                    case "canceled":
                        if (!(lastestStatusOfRequest.equals("pending") || lastestStatusOfRequest.equals("accepted"))) {
                            System.out.println(lastestStatusOfRequest);
                            return new ResponseEntity<>(new ApiResponse(false, "Yêu cầu hiện đang ở trạng thái " + displayLastestStatusRequest + ", vui lòng cập nhật lại"), HttpStatus.OK);
                        } else {
                            requestHistory.setRequestID(requestPresenting.getRequestID());
                            requestHistoryService.save(requestHistory);
                        }
                        break;
                }
            }


            //
            Notification notification = new Notification();
            notification.setUserID(requestPresenting.getUserID());
            notification.setRequestID(ID);
            notification.setAppointmentID("000000");
            notification.setIsRead(0);
            notification.setType("REQUEST");
            //set message of notification
            String status = requestHistory.getStatus();
            switch (status) {
                case "accepted":
                    notification.setMessage("Y tá " + userRepository.findById(requestHistory.getUserID()).get().getName() + " đã nhận đơn xét nghiệm mã " + ID + ". Trạng thái đơn hiện tại: Đang đợi lấy mẫu.");
                    break;
                case "transporting":
                    notification.setMessage("Y tá " + userRepository.findById(requestHistory.getUserID()).get().getName() + " đã hoàn thành lấy mẫu xét nghiệm mã " + ID + ". Trạng thái đơn hiện tại: Đang vận chuyển.");
                    break;
                case "waitingforresult":
                    notification.setMessage("Điều phối viên " + userRepository.findById(requestHistory.getUserID()).get().getName() + " đã tiếp nhận mẫu xét nghiệm mã " + ID + ". Trạng thái đơn hiện tại: Đang đợi kết quả");
                    Notification notiForNurse = new Notification();
                    RequestHistory requestOfNurse = requestHistoryRepository.findAllByRequestIDOrderByCreatedTimeDesc(ID).get(1);
                    notiForNurse.setUserID(requestOfNurse.getUserID());
                    notiForNurse.setRequestID(requestOfNurse.getRequestID());
                    notiForNurse.setAppointmentID("000000");
                    notiForNurse.setIsRead(0);
                    notiForNurse.setType("REQUEST");
                    notiForNurse.setMessage("Điều phối viên " + userRepository.findById(requestHistory.getUserID()).get().getName() + " đã tiếp nhận mẫu xét nghiệm mã " + ID + " từ y tá "
                            + userRepository.findById(requestOfNurse.getUserID()).get().getName());
                    notificationService.saveNoti(notiForNurse);
                    break;
                case "closed":
                    notification.setMessage("Đã hoàn thành kết quả cho mẫu xét nghiệm mã " + ID + ". Trạng thái đơn hiện tại: Đã xong.");
                    break;
                case "canceled":
                    notification.setMessage("Khách hàng " + userRepository.findById(requestHistory.getUserID()).get().getName() + " đã huỷ đơn xét nghiệm có mã " + ID + ". Trạng thái đơn hiện tại: Đã huỷ.");
                    break;
                case "pending":
                    notification.setMessage("Mẫu xét nghiệm mã " + ID + " đang đợi y tá nhận đơn. Trạng thái đơn hiện tại: Đang đợi y tá nhận đơn.");
                    break;
                case "lostsample":
                    notification.setMessage("Mẫu xét nghiệm mã " + ID + " sẽ được lấy lại do lỗi từ y tá " + userRepository.findById(requestHistory.getUserID()).get().getName()
                            + ". Chân thành xin lỗi quý khách! Trạng thái đơn hiện tại: Đang đợi lấy lại mẫu mất do y tá.");
                    break;
                case "coordinatorlostsample":
                    notification.setMessage("Mẫu xét nghiệm mã " + ID + " sẽ được lấy lại do sơ xuất của điều phối viên. Chân thành xin lỗi quý khách! Trạng thái đơn hiện tại: Đang đợi y tá nhận đơn.");
                    break;
                case "reaccepted":
                    notification.setMessage("Y tá " + userRepository.findById(requestHistory.getUserID()).get().getName() + " đã nhận lấy lại đơn mã " + ID + ". Trạng thái đơn hiện tại: Đã nhận lấy lại mẫu mất do điều phối viên.");
                    break;
                case "retransporting":
                    notification.setMessage("Y tá " + userRepository.findById(requestHistory.getUserID()).get().getName() + " đã hoàn thành lấy lại mẫu xét nghiệm mã " + ID + ". Trạng thái đơn hiện tại: Đang vận chuyển mẫu lấy lại.");
                    break;
                case "relostsample":
                    notification.setMessage("Y tá " + userRepository.findById(requestHistory.getUserID()).get().getName() + " đã hoàn thành lấy lại mẫu xét nghiệm mã " + ID + ". Trạng thái đơn hiện tại: Đang vận chuyển mẫu lấy lại.");
                    notification.setMessage("Mẫu xét nghiệm mã " + ID + " sẽ được lấy lại do lỗi từ y tá " + userRepository.findById(requestHistory.getUserID()).get().getName()
                            + ". Chân thành xin lỗi quý khách! Trạng thái đơn hiện tại: Đang đợi lấy lại mẫu mất do điều phối viên làm mất.");
                    break;
            }
            //
            notificationService.saveNoti(notification);
            return new ResponseEntity<>(requestHistory, HttpStatus.OK);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý! Vui lòng kiểm tra lại!"), HttpStatus.OK);
        }
    }

    //detail 1 request
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetailRecentRequest(@PathVariable("id") String requestId) {
        try {
            //Object will return as a request detail
            DetailRequestTestVersionModel detailRequestModel = new DetailRequestTestVersionModel();

            //Check if request existed
            boolean existedRequest = requestRepository.existsByRequestID(requestId);
            if (existedRequest == false) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có yêu cầu với mã ID = " + requestId), HttpStatus.OK);
            }

            //Get all status of the request with ID with descending created time
            List<RequestHistory> lsStatusRequest = requestHistoryService.listRecentStatus(requestId);

            //check if request has no update yet (status = pending) -> a recently created request
            if (lsStatusRequest.isEmpty()) {
                //Request newCreatedRequest = requestRepository.getOne(requestId);
                Request newCreatedRequest = requestService.getRequest(requestId);
                Optional<User> newCreatedRequestUser = userRepository.findById(newCreatedRequest.getUserID());
                Town newCreatedRequestTown = townRepository.getOne(newCreatedRequest.getTownCode());
                District newCreatedRequestDistrict = districtRepository.getOne(newCreatedRequest.getDistrictCode());
                detailRequestModel.setRequestID(requestId); //requestID
                detailRequestModel.setCustomerID(String.valueOf(newCreatedRequest.getUserID())); //customerID
                detailRequestModel.setCustomerName(newCreatedRequestUser.get().getName()); //customerName
                detailRequestModel.setCustomerPhoneNumber(newCreatedRequestUser.get().getPhoneNumber());//customerPhoneNumber
                detailRequestModel.setCustomerDOB(newCreatedRequestUser.get().getDob()); //customerDOB
                detailRequestModel.setRequestAddress(newCreatedRequest.getAddress()); //customer  address
                detailRequestModel.setRequestDistrictID(newCreatedRequest.getDistrictCode());//district code
                detailRequestModel.setRequestDistrictName(newCreatedRequestDistrict.getDistrictName());//district name
                detailRequestModel.setRequestTownID(newCreatedRequest.getTownCode());//town code
                detailRequestModel.setRequestTownName(newCreatedRequestTown.getTownName());//town name
                detailRequestModel.setRequestMeetingTime(newCreatedRequest.getMeetingTime()); //meeting time
                //=====================//
                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String displayCreatedTest = sdf3.format(newCreatedRequest.getCreatedTime());
                String createdTime3 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                //=====================//

                detailRequestModel.setRequestCreatedTime(createdTime3); //created time
                detailRequestModel.setNurseID("Chưa có y tá nhận!");
                detailRequestModel.setNurseName("Chưa có y tá nhận!");
                detailRequestModel.setCoordinatorID("Chưa có điều phối viên xử lý!");
                detailRequestModel.setCoordinatorName("Chưa có điều phối viên xử lý!");
                detailRequestModel.setRequestStatus("pending"); //status
                detailRequestModel.setRequestUpdatedTime(createdTime3);
                //set list selected test
                List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(requestId);
                List<String> lsTestID = new ArrayList<>();
                List<Test> lsTestOfRequest = new ArrayList<>();
                List<Integer> testVersionList = new ArrayList<>();
                long testAmount = 0;
                for (RequestTest tracking : lsRequestTests) {
                    String testID = String.valueOf(tracking.getTestID());
                    testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                    lsTestID.add(testID);
                    lsTestOfRequest.add(testRepository.getOne(tracking.getTestID()));
                    testVersionList.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                }
                System.out.println("Test version " + testVersionList);
                if (testVersionList.isEmpty()) {
                    detailRequestModel.setVersionOfTest(0);
                } else {
                    detailRequestModel.setVersionOfTest(testVersionList.get(0));
                }
                //list test (String)
                detailRequestModel.setLsSelectedTest(lsTestID);
                //set amount of test
                detailRequestModel.setRequestAmount(String.valueOf(testAmount));

                ///*************List test**************//
                List<Integer> lsTestTypeID = new ArrayList<>();
                List<Integer> lsVersionID = new ArrayList<>();
                for (Test testOfRequest_TO_GET_LIST : lsTestOfRequest) {
                    lsTestTypeID.add(testOfRequest_TO_GET_LIST.getTestTypeID());
                    lsVersionID.add(testOfRequest_TO_GET_LIST.getVersionID());
                }
            /*System.out.println("LsTestTypeID" + lsTestTypeID);
            System.out.println("LsVersionID" + lsVersionID);*/

                ///get list test distinct
                List<Integer> lsTestTypeIdDistinct = new ArrayList<>();
                for (Integer testType : lsTestTypeID) {
                    if (!lsTestTypeIdDistinct.contains(testType)) {
                        lsTestTypeIdDistinct.add(testType);
                    }
                }
                //System.out.println("LsDistinct" + lsTestTypeIdDistinct);
                ///////////////////////////

                List<TestTypeListModel> lsResult = new ArrayList<>();
                for (Integer testTypeID : lsTestTypeIdDistinct) {
                    TestTypeListModel testTypeListModel = new TestTypeListModel();
                    testTypeListModel.setTestTypeID(testTypeID);
                    testTypeListModel.setTestTypeName(testTypeService.findTestTypeByID(testTypeID).get().getTestTypeName());
                    List<Test> chosenTest = new ArrayList<>();
                    for (Test test : lsTestOfRequest) {
                        if (test.getTestTypeID() == testTypeID) {
                            chosenTest.add(test);
                        }
                    }
                    testTypeListModel.setListTest(chosenTest);
                    lsResult.add(testTypeListModel);
                }
                detailRequestModel.setDetailListTest(lsResult);
                ///***************************//

                //set note
                detailRequestModel.setRequestNote("Yêu cầu xét nghiệm mới tạo.");
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            else {
                //Get the latest status of request
                RequestHistory requestHistory = lsStatusRequest.get(0);

                //get the latest status which status = accepted -> find nurse
                List<RequestHistory> getListRequestAcceptedNurse =
                        requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(requestHistory.getRequestID(), "accepted");
                if (getListRequestAcceptedNurse.isEmpty() || requestHistory.getStatus().equals("pending")) {
                    detailRequestModel.setNurseID("Chưa có y tá nhận!");
                    detailRequestModel.setNurseName("Chưa có y tá nhận!");
                } else {
                    //get nurse ID
                    detailRequestModel.setNurseID(String.valueOf(getListRequestAcceptedNurse.get(0).getUserID()));
                    //get nurse name
                    detailRequestModel.setNurseName(userRepository.findById(getListRequestAcceptedNurse.get(0).getUserID()).get().getName());
                }

                //get the latest status which status = waitingforresult -> find coordinator
                List<RequestHistory> getListRequestAcceptedCoordinator =
                        requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(requestHistory.getRequestID(), "waitingforresult");
                if (getListRequestAcceptedCoordinator.isEmpty() || requestHistory.getStatus().equals("pending")) {
                    detailRequestModel.setCoordinatorID("Chưa có điều phối viên xử lý!");
                    detailRequestModel.setCoordinatorName("Chưa có điều phối viên xử lý!");
                } else {
                    //get coordinator ID
                    detailRequestModel.setCoordinatorID(String.valueOf(getListRequestAcceptedCoordinator.get(0).getUserID()));
                    //get coordinator name
                    detailRequestModel.setCoordinatorName(userRepository.findById(getListRequestAcceptedCoordinator.get(0).getUserID()).get().getName());
                }
                //=====================//
                SimpleDateFormat sdf10 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String displayCreatedTest10 = sdf10.format(requestHistory.getCreatedTime());
                String createdTime10 = displayCreatedTest10.substring(0, 10) + "T" + displayCreatedTest10.substring(11) + ".000+0000";
                //=====================//
                detailRequestModel.setRequestUpdatedTime(createdTime10);

                //Request nowRequest = requestRepository.getOne(requestHistory.getRequestID());
                Request nowRequest = requestService.getRequest(requestHistory.getRequestID());
                //return detail request
                detailRequestModel.setRequestStatus(requestHistory.getStatus());
                detailRequestModel.setRequestID(nowRequest.getRequestID());
                detailRequestModel.setCustomerID(String.valueOf(nowRequest.getUserID()));
                detailRequestModel.setCustomerName(userRepository.findById(nowRequest.getUserID()).get().getName());
                detailRequestModel.setCustomerPhoneNumber(userRepository.findById(nowRequest.getUserID()).get().getPhoneNumber());
                detailRequestModel.setCustomerDOB(userRepository.findById(nowRequest.getUserID()).get().getDob());
                detailRequestModel.setRequestAddress(nowRequest.getAddress());
                detailRequestModel.setRequestDistrictID(nowRequest.getDistrictCode());
                detailRequestModel.setRequestDistrictName(districtRepository.findById(nowRequest.getDistrictCode()).get().getDistrictName());
                detailRequestModel.setRequestTownID(nowRequest.getTownCode());
                detailRequestModel.setRequestTownName(townRepository.findById(nowRequest.getTownCode()).get().getTownName());
                detailRequestModel.setRequestMeetingTime(nowRequest.getMeetingTime());
                //=====================//
                SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String displayCreatedTest = sdf5.format(nowRequest.getCreatedTime());
                String createdTime5 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                //=====================//

                detailRequestModel.setRequestCreatedTime(createdTime5);

                // get list test
                List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(nowRequest.getRequestID());
                List<String> lsTestID = new ArrayList<>();
                long testAmount = 0;
                List<Test> lsTestOfRequest = new ArrayList<>();
                List<Integer> testVersion = new ArrayList<>();
                for (RequestTest tracking : lsRequestTests) {
                    String testID = String.valueOf(tracking.getTestID());
                    testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                    lsTestID.add(testID);
                    lsTestOfRequest.add(testRepository.getOne(tracking.getTestID()));
                    testVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                }
                System.out.println("Test version " + testVersion);
                if (testVersion.isEmpty()) {
                    detailRequestModel.setVersionOfTest(0);
                } else {
                    detailRequestModel.setVersionOfTest(testVersion.get(0));
                }
                detailRequestModel.setLsSelectedTest(lsTestID);
                //set amount of test
                detailRequestModel.setRequestAmount(String.valueOf(testAmount));


                ///*************List test**************//
                List<Integer> lsTestTypeID = new ArrayList<>();
                List<Integer> lsVersionID = new ArrayList<>();
                for (Test testOfRequest_TO_GET_LIST : lsTestOfRequest) {
                    lsTestTypeID.add(testOfRequest_TO_GET_LIST.getTestTypeID());
                    lsVersionID.add(testOfRequest_TO_GET_LIST.getVersionID());
                }
           /* System.out.println("LsTestTypeID" + lsTestTypeID);
            System.out.println("LsVersionID" + lsVersionID);*/

                ///get list test distinct
                List<Integer> lsTestTypeIdDistinct = new ArrayList<>();
                for (Integer testType : lsTestTypeID) {
                    if (!lsTestTypeIdDistinct.contains(testType)) {
                        lsTestTypeIdDistinct.add(testType);
                    }
                }
                //System.out.println("LsDistinct" + lsTestTypeIdDistinct);
                ///////////////////////////

                List<TestTypeListModel> lsResult = new ArrayList<>();
                for (Integer testTypeID : lsTestTypeIdDistinct) {
                    TestTypeListModel testTypeListModel = new TestTypeListModel();
                    testTypeListModel.setTestTypeID(testTypeID);
                    testTypeListModel.setTestTypeName(testTypeService.findTestTypeByID(testTypeID).get().getTestTypeName());
                    List<Test> chosenTest = new ArrayList<>();
                    for (Test test : lsTestOfRequest) {
                        if (test.getTestTypeID() == testTypeID) {
                            chosenTest.add(test);
                        }
                    }
                    testTypeListModel.setListTest(chosenTest);
                    lsResult.add(testTypeListModel);
                }
                detailRequestModel.setDetailListTest(lsResult);
                ///***************************//

                //setNote
                detailRequestModel.setRequestNote(requestHistory.getNote());
            }
            return new ResponseEntity<>(detailRequestModel, HttpStatus.OK);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //list all request
    @GetMapping("/list-all-request")
    public ResponseEntity<?> getAllRequest() {
        try {
            List<Request> lsAllRequest = requestService.lsRequest();
            if (lsAllRequest.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có yêu cầu xét nghiệm!"), HttpStatus.OK);
            }
            List<DetailRequestModel> returnList = new ArrayList<>();
            for (Request request : lsAllRequest.subList(1, lsAllRequest.size())) {
                String requestId = request.getRequestID();
                //Object will return as a request detail
                DetailRequestModel detailRequestModel = new DetailRequestModel();

                //Get all status of the request with ID with descending created time
                List<RequestHistory> lsStatusRequest = requestHistoryService.listRecentStatus(requestId);

                //check if request has no update yet (status = pending) -> a recently created request
                if (lsStatusRequest.isEmpty()) {
                    //Request newCreatedRequest = requestRepository.getOne(requestId);
                    Request newCreatedRequest = requestService.getRequest(requestId);
                    Optional<User> newCreatedRequestUser = userRepository.findById(newCreatedRequest.getUserID());
                    Town newCreatedRequestTown = townRepository.getOne(newCreatedRequest.getTownCode());
                    District newCreatedRequestDistrict = districtRepository.getOne(newCreatedRequest.getDistrictCode());
                    detailRequestModel.setRequestID(requestId); //requestID
                    detailRequestModel.setCustomerID(String.valueOf(newCreatedRequest.getUserID())); //customerID
                    detailRequestModel.setCustomerName(newCreatedRequestUser.get().getName()); //customerName
                    detailRequestModel.setCustomerPhoneNumber(newCreatedRequestUser.get().getPhoneNumber());//customerPhoneNumber
                    detailRequestModel.setCustomerDOB(newCreatedRequestUser.get().getDob()); //customerDOB
                    detailRequestModel.setRequestAddress(newCreatedRequest.getAddress()); //customer  address
                    detailRequestModel.setRequestDistrictID(newCreatedRequest.getDistrictCode());//district code
                    detailRequestModel.setRequestDistrictName(newCreatedRequestDistrict.getDistrictName());//district name
                    detailRequestModel.setRequestTownID(newCreatedRequest.getTownCode());//town code
                    detailRequestModel.setRequestTownName(newCreatedRequestTown.getTownName());//town name
                    detailRequestModel.setRequestMeetingTime(newCreatedRequest.getMeetingTime()); //meeting time
                    //=====================//
                    SimpleDateFormat sdk = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String displayCreatedTest = sdk.format(newCreatedRequest.getCreatedTime());
                    String createdTimesdk = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                    //=====================//

                    detailRequestModel.setRequestCreatedTime(createdTimesdk); //created time
                    detailRequestModel.setRequestUpdatedTime(createdTimesdk);
                    detailRequestModel.setNurseID("Chưa có y tá nhận!");
                    detailRequestModel.setNurseName("Chưa có y tá nhận!");
                    detailRequestModel.setCoordinatorID("Chưa có điều phối viên xử lý!");
                    detailRequestModel.setCoordinatorName("Chưa có điều phối viên xử lý!");
                    detailRequestModel.setRequestStatus("pending"); //status
                    //set list selected test
                    List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(requestId);
                    List<String> lsTestID = new ArrayList<>();
                    List<Integer> lsVersionTest = new ArrayList<>();
                    long testAmount = 0;
                    for (RequestTest tracking : lsRequestTests) {
                        System.out.println(tracking.getTestID());
                        String testID = String.valueOf(tracking.getTestID());
                        testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                        lsTestID.add(testID);
                        lsVersionTest.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                    }
                    detailRequestModel.setLsSelectedTest(lsTestID);
                    //set amount of test
                    detailRequestModel.setRequestAmount(String.valueOf(testAmount));
                    //set note
                    detailRequestModel.setRequestNote("Yêu cầu xét nghiệm mới tạo.");
                    //detailRequestModel.setVersionOfTest(lsVersionTest.get(0));
                    if (lsVersionTest.isEmpty()) {
                        detailRequestModel.setVersionOfTest(0);
                    } else {
                        detailRequestModel.setVersionOfTest(lsVersionTest.get(0));
                    }
                }
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else {
                    //Get the latest status of request
                    RequestHistory requestHistory = lsStatusRequest.get(0);

                    //get the latest status which status = accepted -> find nurse
                    List<RequestHistory> getListRequestAcceptedNurse =
                            requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(requestHistory.getRequestID(), "accepted");
                    if (getListRequestAcceptedNurse.isEmpty() || requestHistory.getStatus().equals("pending")) {
                        detailRequestModel.setNurseID("Chưa có y tá nhận!");
                        detailRequestModel.setNurseName("Chưa có y tá nhận!");
                    } else {
                        //get nurse ID
                        detailRequestModel.setNurseID(String.valueOf(getListRequestAcceptedNurse.get(0).getUserID()));
                        //get nurse name
                        detailRequestModel.setNurseName(userRepository.findById(getListRequestAcceptedNurse.get(0).getUserID()).get().getName());
                    }

                    //get the latest status which status = waitingforresult -> find coordinator
                    List<RequestHistory> getListRequestAcceptedCoordinator =
                            requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(requestHistory.getRequestID(), "waitingforresult");
                    if (getListRequestAcceptedCoordinator.isEmpty() || requestHistory.getStatus().equals("pending")) {
                        detailRequestModel.setCoordinatorID("Chưa có điều phối viên xử lý!");
                        detailRequestModel.setCoordinatorName("Chưa có điều phối viên xử lý!");
                    } else {
                        //get coordinator ID
                        detailRequestModel.setCoordinatorID(String.valueOf(getListRequestAcceptedCoordinator.get(0).getUserID()));
                        //get coordinator name
                        detailRequestModel.setCoordinatorName(userRepository.findById(getListRequestAcceptedCoordinator.get(0).getUserID()).get().getName());
                    }
                    //=====================//
                    SimpleDateFormat sdf69 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String displayCreatedTest69 = sdf69.format(requestHistory.getCreatedTime());
                    String createdTime69 = displayCreatedTest69.substring(0, 10) + "T" + displayCreatedTest69.substring(11) + ".000+0000";
                    //=====================//
                    detailRequestModel.setRequestUpdatedTime(createdTime69);

                    //Request nowRequest = requestRepository.getOne(requestHistory.getRequestID());
                    Request nowRequest = requestService.getRequest(requestHistory.getRequestID());
                    //return detail request
                    detailRequestModel.setRequestStatus(requestHistory.getStatus());
                    detailRequestModel.setRequestID(nowRequest.getRequestID());
                    detailRequestModel.setCustomerID(String.valueOf(nowRequest.getUserID()));
                    detailRequestModel.setCustomerName(userRepository.findById(nowRequest.getUserID()).get().getName());
                    detailRequestModel.setCustomerPhoneNumber(userRepository.findById(nowRequest.getUserID()).get().getPhoneNumber());
                    detailRequestModel.setCustomerDOB(userRepository.findById(nowRequest.getUserID()).get().getDob());
                    detailRequestModel.setRequestAddress(nowRequest.getAddress());
                    detailRequestModel.setRequestDistrictID(nowRequest.getDistrictCode());
                    detailRequestModel.setRequestDistrictName(districtRepository.findById(nowRequest.getDistrictCode()).get().getDistrictName());
                    detailRequestModel.setRequestTownID(nowRequest.getTownCode());
                    detailRequestModel.setRequestTownName(townRepository.findById(nowRequest.getTownCode()).get().getTownName());
                    detailRequestModel.setRequestMeetingTime(nowRequest.getMeetingTime());
                    //=====================//
                    SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String displayCreatedTest = sdf4.format(nowRequest.getCreatedTime());
                    String createdTime4 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                    //=====================//
                    detailRequestModel.setRequestCreatedTime(createdTime4);
                    // get list test
                    List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(nowRequest.getRequestID());
                    List<String> lsTestID = new ArrayList<>();
                    List<Integer> lsVersionOfTest = new ArrayList<>();
                    long testAmount = 0;
                    for (RequestTest tracking : lsRequestTests) {
                        System.out.println(tracking.getTestID());
                        String testID = String.valueOf(tracking.getTestID());
                        testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                        lsVersionOfTest.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                        lsTestID.add(testID);
                    }
                    detailRequestModel.setLsSelectedTest(lsTestID);
                    //set amount of test
                    detailRequestModel.setRequestAmount(String.valueOf(testAmount));
                    //setNote
                    detailRequestModel.setRequestNote(requestHistory.getNote());
                    //set version
                    if (lsVersionOfTest.isEmpty()) {
                        detailRequestModel.setVersionOfTest(0);
                    } else {
                        detailRequestModel.setVersionOfTest(lsVersionOfTest.get(0));
                    }
                }
                returnList.add(detailRequestModel);
            }
            if (returnList.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Hiện tại chưa có đơn xét nghiệm nào!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(returnList, HttpStatus.OK);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //get list result of a request
    @GetMapping("/detail/{id}/result")
    public ResponseEntity<?> getListResult(@PathVariable("id") String requestID) {
        try {
            //Optional<Request> request = requestRepository.findById(requestID);
            Request request = requestService.getRequest(requestID);
            if (request == null) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại yêu cầu xét nghiệm với mã ID = " + requestID), HttpStatus.OK);
            }
            List<Result> lsResult = resultService.lsResultByRequestID(requestID);
            if (lsResult.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Chưa có kết quả cho yêu cầu xét nghiệm này!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsResult, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //update result for a request
    @PostMapping("/detail/results/add")
    public ResponseEntity updateResultOfRequest(@RequestBody ResultModel resultModel) {
        try {
            List<String> lsImage = resultModel.getListImage();
            List<Result> lsCreatedResult = new ArrayList<>();
            for (String image : lsImage) {
                Result result = new Result();
                result.setImage(image);
                result.setRequestID(resultModel.getRequestID());
                result.setUserID(resultModel.getUserID());
                resultService.saveResult(result);
                lsCreatedResult.add(result);
            }
            List<ReturnResult> lsReturnRequest = new ArrayList<>();
            //=====================//
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Result result : lsCreatedResult) {
                String displayCreatedTest = sdf3.format(result.getCreatedTime());
                String createdTime3 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                ReturnResult returnResult = new ReturnResult();
                returnResult.setResultID(result.getResultID());
                returnResult.setCreatedTime(createdTime3);
                returnResult.setUserID(result.getUserID());
                returnResult.setRequestID(result.getRequestID());
                returnResult.setImage(result.getImage());
                lsReturnRequest.add(returnResult);
            }
            //=====================//
            return new ResponseEntity(lsReturnRequest, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

}
