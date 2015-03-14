/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package quy.datamining.malletapi.lda;

/**
 *
 * @author Quy
 */
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class InferTopicModel {
	public static ParallelTopicModel model = null;
	public static InstanceList instances=null;    
    TopicInferencer inferencer = null;
    private static InferTopicModel inferTopicModel = null;
    Runtime runtime = Runtime.getRuntime();
	public InferTopicModel() {

    } 
	public static InferTopicModel getInstance(String modelName, String trainFile) {
		if (inferTopicModel == null) {
			inferTopicModel = new InferTopicModel(modelName, trainFile);
		}
		return inferTopicModel;
	}
    
	public InferTopicModel(String modelName, String trainFile) {
        try {            
            // Begin by importing documents from text to feature sequences
            ArrayList pipeList = new ArrayList();
            // Pipes: lowercase, tokenize, remove stopwords, map to features
            pipeList.add( new CharSequenceLowercase() );
            pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
            pipeList.add( new TokenSequence2FeatureSequence() );
            instances = new InstanceList(new SerialPipes(pipeList));
            Reader fileReader = new InputStreamReader(new FileInputStream(new File(trainFile)), "UTF-8");
            instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                                                   3, 2, 1)); 
            //instances.clear();
            System.out.println("Data mem (mb): " + (runtime.getRuntime().totalMemory() - runtime.getRuntime().freeMemory())/1024/1024);
			FileInputStream in = new FileInputStream(modelName);
			ObjectInputStream ois = new ObjectInputStream(in);			
			model= (ParallelTopicModel) (ois.readObject());	
            inferencer = model.getInferencer();
            /*
            String test1 = "Man_Utd phá vỡ hợp_đồng chiêu_mộ Argentina  Xem bóng_đá trực_tuyến phiên_bản di_động";
            getTopicDitribution(test1);*/
            System.out.println("Number of words of models: " + model.getAlphabet().size());            
            //instances.clear();
            System.out.println("Data mem (mb): " + (runtime.getRuntime().totalMemory() - runtime.getRuntime().freeMemory())/1024/1024);
            //instances.clear();
            int size = instances.size();     
            System.out.println("Size: " + size);
            for(int i =size-1; i >=0; i--){                
                instances.remove(i);
            }
            System.out.println("Size: " + instances.size());
            System.out.println("Data mem (mb): " + (runtime.getRuntime().totalMemory() - runtime.getRuntime().freeMemory())/1024/1024);
            
		} catch (Exception e) {
			System.out.println("Problem serializing: " + e);
            e.printStackTrace();
		}
		System.out.println("Topic Model is read.");
	}

	public double[] getTopicDitribution(String text) {
        //InstanceList _instances = (InstanceList) instances.clone();
        int size = instances.size();        
        for(int i =size-1; i >=0; i--){
            instances.remove(i);
        }
		Instance ins=new Instance(text,null,null,null);
		instances.addThruPipe(ins);
		inferencer.setRandomSeed(1);
		double results[]=inferencer.getSampledDistribution(instances.get(0), 10, 1, 5);
        instances.remove(0);
		return results;
	}
    
    public double[] getTopicDitributionFromPipe(String text) {
        System.out.println("Data mem (mb): " + (runtime.getRuntime().totalMemory() - runtime.getRuntime().freeMemory())/1024/1024);
        InstanceList instancesList = new InstanceList(instances.getPipe());
        
		Instance ins=new Instance(text,null,null,null);
		instancesList.addThruPipe(ins);
		inferencer.setRandomSeed(1);
		double results[]=inferencer.getSampledDistribution(instancesList.get(0), 10, 1, 5);
		return results;
	}
    
    public static void main(String[] args){
        RevertEntryIntDoubleComparator m_revertEntryIntDoubleComparator = new RevertEntryIntDoubleComparator();
        String modelName = "data/model148k/model.txt.1000";
        String trainFile = "data/sample_vi_148k.txt";
        InferTopicModel inferTopicModel = InferTopicModel.getInstance(modelName, trainFile); 
        //test 1
        long begin = System.currentTimeMillis(); 
        String processedText = "Lật xe , 5 chiến_sĩ hi_sinh trên đường làm nhiệm_vụ Hình_ảnh mới nhất cứu_hộ vụ sập hầm thủy_điện Đạ_Dâng Tang_thương xóm cào ngao thuê Ngày thứ_hai cứu_hộ sập hầm thủy_điện Đạ_Dâng : Nước ngập , đào đất khó_khăn Có con từ ... bụng người khác , phải làm_sao ? 12 người mắc_kẹt còn sống , lực_lượng cứu_hộ chuẩn_bị vào hầm Nước Úc chưa hết bàng_hoàng sau vụ bắt_cóc con_tin Hai học_sinh giả chết để qua mắt tay súng Taliban Cách giải_cứu tai_nạn hầm_mỏ và phép màu 2010   2014 : 66 nhà_báo bị giết , 119 người   bị bắt_cóc Taliban tấn_công trường_học bắt con_tin , 84 người chết Quan tham Trung_Quốc đam_mê nhận hối_lộ ngọc quý Tình_tiết mới trong vụ nam_sinh bị kết_án tù vì giật mũ Phạt tử_tù Lê_Thị_Hường thêm 5 năm tù Huỳnh_Thị_Huyền_Như   nói không kháng_cáo Y_án 30 năm tù đối_với “ bầu ” Kiên 8 bị_cáo chặt cây keo lai :   trả tự_do 5 bị_cáo Hộ kinh_doanh vẫn được mua hóa_đơn Khóc vì thuốc tạo trầm dỏm “ Ép ” tiểu_thương ... phải làm giám_đốc Đồng rúp Nga sụt_giá kỉ_lục Phía sau cơn bão Uber :   Để đi tới điểm “ ai cũng có lợi ” Giá USD nhảy_múa loạn_xạ   Đừng chủ_quan với thoát_vị bẹn Có con từ ... bụng người khác , phải làm_sao ? Sức_khỏe của bạn : Dinh_dưỡng cho người bị gan nhiễm mỡ Ăn một con ốc lạ ,   bé 6 tuổi chết Để có con từ bụng ... người khác Phát_hiện \" siêu_vi_khuẩn \" ở vùng_biển sẽ diễn ra Olympic   Màu_sắc Việt_Nam hay quốc_tế_hóa ? Một kỳ thi :   Cơ_hội và thách_thức của nhà_trường Đào_tạo cử_tuyển ít nhưng không sử_dụng hết Có dấu_hiệu nhờ thi hộ , một thí_sinh từ đỗ hóa trượt Giáo_dục phải có chất_lượng   Viết sách_giáo_khoa : Từ những bộ sách âm_thầm ... Tuổi thanh_xuân mang phong_cách Việt - Hàn Nghiên_cứu   Hoàng_Sa - Trường_Sa   từ lịch_sử Trung_Quốc Công_bố 10 sự_kiện âm_nhạc tiêu_biểu 2014 Phạt NXB Văn_hóa thông_tin 21 triệu đồng Tuổi 20 sóng_gió của ca_sĩ Sơn_Tùng Nhiê ̀ u y ́ kiê ́ n vê ̀ chuyê ̣ n \" gia ̉ ga ́ i trên truyê ̀ n hi ̀ nh \" Á_hậu Diễm_Trang tham_gia tiếp_sức nghị_lực trên đường chạy Tuyên_dương 183 “ SV 5 tốt ” ÐHQG_TP . HCM Thành_công không đến dễ_dàng Bình_chọn \" Công_dân trẻ tiêu_biểu TP.HCM \" 2014 Người_Việt tìm Flappy_Bird , Doraemon , Lệ_Rơi nhiều nhất trên Google 2014 ? Facebook chuẩn_bị 7 nút mới bên_cạnh \" Like \" Bộ_gõ   WinVNKey : gõ tắt mà bung ra chữ Việt trọn_vẹn Chiếc smartphone giá 6.000 USD cho các \" fan \" Lamborghini YouTube vừa có thêm tính_năng hay nhất năm nay Các thành_phố Mỹ phát_triển dịch_vụ taxi Internet 200 thư xin việc cũng thừa nếu_không gây ấn_tượng Bạn_đọc giúp cô học_trò bị ung_thư nuôi mẹ tâm_thần Bán hàng trên đường_cao_tốc gây bất_an Chặn hành_vi xấu trên xe_buýt Có con từ ... bụng người khác , phải làm_sao ? Bất_ngờ với thành_phố dưới biển của Nhật Du_lịch năm 2015 có gì mới ? Chùm ảnh vui_nhộn với ông_già Noel khắp_nơi   Chinh_phục những \" cầu_thang lên thiên_đường \" nổi_tiếng thế_giới Khu_vực sông Mekong vào top 50 điểm đến hấp_dẫn nhất 2015 Ca sinh 5 tại Việt_Nam vẫn đang phát_triển khỏe_mạnh Tinh_thần tiên_phong và đam_mê sáng_tạo từ người Nhật Khai_giảng các khóa học Kế_toán_trưởng - Kế_toán thuế - Quản_trị Dịp_Tết_Bibica ra_mắt bánh Lạc_Việt với vỏ hộp trống_đồng Ngọc_Lũ   Công_ty Bếp_Chiên chuyên cung_cấp tủ nấu hấp cơm công_nghiệp   Người_dân TP.HCM nô_nức dự khai_trương trung_tâm mua_sắm Robins Lật xe , 5 chiến_sĩ hi_sinh trên đường làm nhiệm_vụ Má_hồng chả thích ưu_tiên Chợ_phiên Sài_Gòn : nhộn_nhịp từ sáng đến đêm Bất_ngờ với thành_phố dưới biển của Nhật Giáo_dục Cẩn_trọng khi cho trẻ sử_dụng máy_tính bảng TT - Cần phân_biệt hai loại thiết_bị đang có trên thị_trường : máy_tính bảng có cài_đặt thêm sách_giáo_khoa , các ứng_dụng dạy - học và công_cụ giáo_dục điện_tử . Máy_tính bảng AIC Group có tên là Smart_Education với màn_hình 7,85 inch với độ_phân_giải 1024x768 , CPU dual core , bộ_nhớ lưu_trữ 8GB , camera chính 3Mp , camera phụ 2Mp , kết_nối mạng WiFi , pin 3.600mAh , hệ_điều_hành Android - Ảnh : T . T . D . Lộ máy_tính bảng giáo_dục AIC giá bèo Máy_tính bảng thay sách_giáo_khoa : chưa lường hết tiêu_cực Hàn_Quốc : vẫn đang thử_nghiệm sách_giáo_khoa điện_tử Cùng là cây_bút để viết , nhưng ở mỗi cấp học có nhu_cầu và cách sử_dụng khác_nhau . Máy_tính bảng cũng vậy . Ở bậc tiểu_học , đặc_biệt là những lớp đầu đời , việc sử_dụng máy_tính bảng rất hạn_chế . Nó chỉ là một công_cụ hỗ_trợ bổ_sung , thậm_chí như một món đồ_chơi công_nghệ . Dùng máy_tính bảng quá sớm và những hệ_lụy Chỉ nói tới một chuyện là tập viết chữ . Những năm đầu tiểu_học , học_sinh cần phải được học nắn_nót viết chữ cho đúng và đẹp . Không phải ngẫu_nhiên mà các nhà sư_phạm từ ngàn_xưa và từ Đông sang Tây đều coi việc tập viết chữ cho trẻ_em là vô_cùng quan_trọng - là chìa_khóa cho cả một cuộc_đời học_tập sau_này . Rèn chữ không đơn_giản chỉ là viết cho đúng , cho đẹp , mà_còn là rèn cả nết người ( biết kiên_nhẫn , tỉ_mỉ , có óc thẩm_mỹ ... ) . Từ khi có máy_tính_cá_nhân , ngay cả những người trước_đây có nét chữ rất đẹp , nay xài máy_tính quen rồi nên viết chữ xấu như gà bới . Vì_thế , ý_định cho trẻ tập viết trên máy_tính bảng , hay bảng tương_tác , là điều không_tưởng . Vì_sao xưa_nay người_ta khuyến_khích học_sinh chép bài , ít_nhất cũng là chép tóm_lược bài_học ? Bởi khi chép bài như_vậy , học_sinh tập_trung vào bài_học hơn và nhớ lâu hơn . Đó còn là một phương_pháp để rèn_luyện và phát_triển trí_nhớ cho con_người ngay từ thời nhỏ_tuổi . Vì_thế , việc dùng bộ_nhớ máy_tính thay cho bộ_nhớ con_người là lợi_bất_cập_hại . Trẻ_em có_thể tiếp_cận máy_tính bảng từ tuổi nào cũng được , nhưng là với chức_năng như một món đồ_chơi . Còn sử_dụng máy_tính bảng như một công_cụ học_tập chính_thức thì_phải ở một độ tuổi nhất_định nào_đó , do các nhà chuyên_môn nghiên_cứu và khuyến_cáo . Có vô_số hệ_lụy mà người_lớn có_thể lường trước được do trẻ_em lạm_dụng máy_tính các loại . Trước_hết là mắt sẽ bị ảnh_hưởng nặng với nguy_cơ bị các tật_bệnh về mắt như khúc_xạ , khô mắt ... cao hơn . Rồi những tia bức_xạ nguy_hiểm cho cơ_thể do tiếp_cận gần_gũi và thường_xuyên với thiết_bị điện_tử . Học_sinh trung_học , thậm_chí ngay cả sinh_viên đại_học , còn dễ bị trộm_cắp hay bị cướp máy_tính thì nói chi tới học_sinh tiểu_học ! Quan_trọng nhất_là màn_hình Không phải là một công_cụ giáo_dục điện_tử Xét về mọi khía_cạnh , mẫu máy_tính bảng Smart_Education nhãn_hiệu AIC Group vừa xuất_hiện ở VN thực_chất chỉ là một máy_tính bảng có cài thêm những cuốn sách_giáo_khoa điện_tử mà thôi . Nó không phải là một công_cụ giáo_dục điện_tử cả về nội_dung lẫn thiết_bị . Cấu_hình của nó rất thấp và nhất_là màn_hình - thành_phần quan_trọng nhất - lại có chất_lượng cực thấp : màn_hình chỉ 7,85 inch , độ_phân_giải thấp 976 x 768 pixel , độ sáng và tương_phản yếu , không sắc nét , góc nhìn hẹp , độ cảm_ứng không nhạy . Nó chạy hệ_điều_hành cũ Android 4.2.2 , không có giao_diện riêng cho một công_cụ giáo_dục điện_tử . Không phải máy_tính bảng nào cũng có_thể được dùng cho học_sinh . Ở các nước tiên_tiến , tất_cả vật_dụng cho trẻ_em đều phải bảo_đảm các tiêu_chuẩn riêng . Máy_tính bảng là một thiết_bị nghe_nhìn , càng có những tiêu_chuẩn nghiêm_ngặt hơn . Đại_học Kentucky của Mỹ đưa ra tiêu_chuẩn tối_thiểu của máy_tính bảng dành cho sinh_viên là có CPU dual - core 1,3 GHz hay Apple A5 ; bộ_nhớ lưu_trữ 32GB ; kết_nối không dây WiFi 802.11n ; có cổng USB và nếu có thêm cổng HDMI càng tốt để dễ kết_nối với màn_hình lớn , máy_chiếu hay bảng tương_tác . Trong khi đó Bộ Giáo dục bang Florida ( Mỹ ) vừa đưa ra hướng_dẫn công_nghệ cho nhà_trường được áp_dụng tới năm_học 2018-2019 là máy_tính bảng phải có màn_hình từ 9,5 inch trở_lên với độ_phân_giải tối_thiểu 1024 x 768 pixel ; RAM ít_nhất 1GB ; chạy hệ_điều_hành Android 4.0 trở_lên ... Họ lưu_ý là không chấp_nhận sử_dụng máy_tính bảng có màn_hình dưới 9,5 inch cho học_sinh . Trong các thành_phần của máy_tính bảng , quan_trọng nhất_là màn_hình . Nhà_sản_xuất có_thể tiết_kiệm chỗ nào thì tùy , chứ không được đụng tới chất_lượng màn_hình . Màn_hình phải dùng loại có chất_lượng cao , màu_sắc chính_xác , độ tương_phản và độ sáng tốt , sắc nét , góc nhìn rộng và ít hại mắt . Nếu không_thể sử_dụng màn_hình công_nghệ OLED ( diode tự_phát sáng hữu_cơ ) do còn quá đắt , màn_hình ít_nhất cũng phải dùng tấm nền IPS với đèn nền thuộc công_nghệ LED . Đặc_biệt với trẻ_em vốn hiếu_động , màn_hình phải cứng_cáp . Các cấu_hình khác là CPU tệ nhất_là hai nhân ( dual - core ) để vừa mạnh , vừa hỗ_trợ tốt tính_năng đa_nhiệm ; bộ_nhớ RAM thấp nhất cũng là 1GB ( nếu chạy hệ_điều_hành Windows thì cần bộ_nhớ RAM từ 2GB ) ; bộ_nhớ lưu_trữ trong từ 8GB trở_lên ( nếu chạy Windows thì cần dung_lượng từ 32GB trở_lên ) ; hỗ_trợ thẻ nhớ ngoài . Phải có kết_nối không dây WiFi chuẩn 802.11b / n . Có microphone và loa . Có máy_ảnh tương_đối tốt ở cả hai mặt . Nên chọn mô_hình trường_lớp thông_minh Cần phân_biệt hai loại thiết_bị đang có trên thị_trường : máy_tính bảng có cài_đặt thêm sách_giáo_khoa , các ứng_dụng dạy và học ; và công_cụ giáo_dục điện_tử . Với máy_tính bảng bình_thường , ai cũng có_thể cài_đặt thêm các sách_giáo_khoa và ứng_dụng giáo_dục mà mình mua . Còn với công_cụ giáo_dục điện_tử ( trong đó có sách_giáo_khoa điện_tử ) , thiết_bị phải có giao_diện người dùng riêng và với những tính_năng quản_lý phù_hợp . Với thiết_bị thứ_hai này , học_sinh chỉ được phép sử_dụng các nội_dung giáo_dục được cài_đặt sẵn và thực_hiện các tác_vụ được phụ_huynh hay thầy_cô cho_phép ( nghĩa_là có kiểm_soát ) . Theo thiển_ý của tôi , trong điều_kiện VN bây_giờ , đừng vội đặt ra mục_đích là dùng máy_tính bảng để thay_thế sách_giáo_khoa và các công_cụ dạy và học trong nhà_trường . Ngay cả các nước tiên_tiến và giàu_có cũng chẳng dám mơ như_vậy . Máy_tính bảng hay công_cụ giáo_dục điện_tử vẫn chỉ nên là một công_cụ hỗ_trợ mang tính tùy chọn . Không ai phản_đối những trường “ đặc_biệt ” ( như trường tư dành cho đối_tượng phụ_huynh có thu_nhập cao ) ứng_dụng máy_tính bảng cho việc dạy và học như một nét riêng của trường mình và được phụ_huynh chấp_nhận . Chuyện ứng_dụng công_cụ này ra_sao và ở mức_độ nào lại là chuyện khác . Cũng chẳng ai phủ_nhận các tính_năng hữu_ích của loại_hình sách_giáo_khoa điện_tử , công_cụ học_tập điện_tử . Nhưng cũng chẳng ai đem sách_giáo_khoa điện_tử đi so_sánh và phủ_nhận sách_giáo_khoa truyền_thống . Nếu phải lựa_chọn , tôi vẫn thích mô_hình những trường_lớp thông_minh , nơi thầy_trò có_thể tiếp_cận các công_nghệ hiện_đại để nâng cao chất_lượng dạy và học của mình . Bên_cạnh đó , Nhà_nước nên có những chương_trình phát_triển các công_cụ giáo_dục điện_tử chất_lượng cao với giá rẻ hết_mức có_thể được để tạo điều_kiện có nhiều học_sinh được tiếp_cận với công_cụ thời_đại này và được kết_nối cùng cộng_đồng . Phóng to Nhà_báo Phạm_Hồng_Phước tìm_hiểu máy_tính bảng của AIC - Ảnh : Đức_Thiện Phạm_Hồng_Phước là nhà_báo chuyên về mảng công_nghệ và quốc_tế . Ông là thành_viên trong nhóm sáng_lập tạp_chí e - CHIP và giữ chức phó_tổng biên_tập phụ_trách tạp_chí e - CHIP tới năm 2008 . Hiện_nay , ông là chủ_biên của Siêu_Thị_Số e-magazine và trang tin công_nghệ Media_Online . Ngoài danh_hiệu Hiệp_sĩ công_nghệ_thông_tin do e - CHIP trao , ông còn liên_tiếp tám năm liền ( 2007-2014 ) nhận giải_thưởng Most_Valuable_Professional ( MVP - tạm dịch : Chuyên_viên có giá_trị nhất ) do Tập_đoàn Microsoft trao cho những người có đóng_góp , chia_sẻ tri_thức công_nghệ - đặc_biệt là về các sản_phẩm Microsoft - cho cộng_đồng . ĐỨC_THIỆN Muốn tăng kỹ_năng xã_hội : tránh xa máy_tính bảng Nghiên_cứu mới_đây của ĐH California ở Los_Angeles ( Mỹ ) cho thấy trẻ_em sẽ cải_thiện đáng_kể khả_năng giao_tiếp , nhận_biết cảm_xúc và các kỹ_năng xã_hội nếu tránh xa điện_thoại thông_minh và máy_tính bảng trong năm ngày liên_tiếp . Các nhà_nghiên_cứu đã tập_hợp 50 trẻ lớp 6 và tổ_chức một trại_hè ngoài thiên_nhiên cho các em , với một quy_định duy_nhất là cấm tất_cả các thiết_bị công_nghệ_cao . “ Ý_tưởng của nghiên_cứu này xuất_hiện khi tôi thấy con_gái lớn của mình và các em của nó giao_tiếp với nhau - báo Los_Angeles_Times dẫn lời chuyên_gia Yalda_Uhls , đồng_tác_giả nghiên_cứu - Thay_vì nói_chuyện và nhìn vào mặt nhau , chúng chỉ nhắn_tin và nhìn chằm_chằm vào điện_thoại ” . Chuyên_gia Uhls và giáo_sư Patricia_Greenfield cho_biết họ muốn quan_sát cách trẻ nhỏ giao_tiếp với nhau mà hoàn_toàn không sử_dụng công_nghệ . Ở trại_hè kéo_dài năm ngày , các em học_sinh không được tiếp_cận các thiết_bị điện_tử . Các em được yêu_cầu thực_hiện hai thử_nghiệm để đo_lường khả_năng nhận_biết xã_hội . Trong thử_nghiệm thứ nhất , các em được yêu_cầu đánh_giá cảm_xúc trên 48 bức ảnh chụp mặt người khác_nhau . Trong thử_nghiệm thứ_hai , các em xem một đoạn băng tắt tiếng , và sau đó đánh_giá về cảm_xúc của những nhân_vật xuất_hiện trong đó . Vào cuối trại_hè , các em làm lại bài trắc_nghiệm . Các nhà_nghiên_cứu thấy rằng trong khoảng thời_gian năm ngày , các em đã cải_thiện lỗi trong bài nhận_biết cảm_xúc qua ảnh từ trung_bình 14,02 lỗi xuống còn 9,41 lỗi , trong khi với bài trắc_nghiệm qua băng ghi_hình , tỉ_lệ đánh_giá đúng của các em tăng từ 26% lên 31% . “ Chúng_tôi ngạc_nhiên vì chỉ năm ngày lại tạo ra khác_biệt lớn đến thế - chuyên_gia Uhls cho_biết - Thiếu giao_tiếp trực_diện đang thay_đổi khả_năng nhận_biết cảm_xúc của con_người ... Tôi thích công_nghệ , các con tôi cũng thế , nhưng điều quan_trọng là phải cân_bằng được cuộc_sống ” . HẢI_MINH _ _ _ _ _ _ _ _ _ Tin bài liên_quan : SGK điện_tử : Phải có hội_đồng khoa_học thẩm_định Trẻ lớp 1 , 2 , 3 có cần máy_tính bảng ? Sắm 320.000 máy_tính bảng : cần tham_khảo phụ_huynh Đề_án máy_tính bảng , thêm gánh nặng cho học_sinh ? Phải sắm 320.000 máy_tính bảng máy_tính bảng tiểu_học đổi_mới giáo_dục Sách_giáo_khoa điện_tử AIC Group Màu_sắc Việt_Nam hay quốc_tế_hóa ? Một kỳ thi :   Cơ_hội và thách_thức của nhà_trường Đào_tạo cử_tuyển ít nhưng không sử_dụng hết Có dấu_hiệu nhờ thi hộ , một thí_sinh từ đỗ hóa trượt Lật xe , 5 chiến_sĩ hi_sinh trên đường làm nhiệm_vụ Hình_ảnh mới nhất cứu_hộ vụ sập hầm thủy_điện Đạ_Dâng Tang_thương xóm cào ngao thuê Ngày thứ_hai cứu_hộ sập hầm thủy_điện Đạ_Dâng : Nước ngập , đào đất khó_khăn Tình_tiết mới trong vụ nam_sinh bị kết_án tù vì giật mũ Phạt tử_tù Lê_Thị_Hường thêm 5 năm tù Huỳnh_Thị_Huyền_Như   nói không kháng_cáo Nước Úc chưa hết bàng_hoàng sau vụ bắt_cóc con_tin Hai học_sinh giả chết để qua mắt tay súng Taliban Cách giải_cứu tai_nạn hầm_mỏ và phép màu 2010   2014 : 66 nhà_báo bị giết , 119 người   bị bắt_cóc Hộ kinh_doanh vẫn được mua hóa_đơn Khóc vì thuốc tạo trầm dỏm “ Ép ” tiểu_thương ... phải làm giám_đốc Đồng rúp Nga sụt_giá kỉ_lục Biển và nhận_thức con_người 15 loài động_vật dễ_thương nhưng bạn phải ... tránh xa ! Ngắm ảnh động_vật di_cư đẹp như tranh Phát_hiện những điều kỳ_lạ trên sao Hỏa Ảnh không_gian ấn_tượng năm 2014 Nữ được ưu_tiên trúng_tuyển nếu bằng điểm thí_sinh nam Vào ngành văn không cần thi văn Rút ngắn đường đến trường_thi Nhiều trường nghề xây hàng chục tỉ đồng   nhưng vắng học_sinh Thanh_tra đột_xuất trường ĐH_Y dược Hải_Phòng Điểm học_tập trung_bình , có_thể du_học Mỹ ? Cơ_hội nhận học_bổng tại ĐH  Curtin_Singapore Quỹ giáo_dục VN cấp 34 học_bổng sau ĐH tại Mỹ Học_bổng du_học ĐH Latrobe , Úc Hungary tăng gấp đôi   số học_bổng cho SV Việt_Nam Câu_chuyện giáo_dục :   Bài_học từ hươu Ước_mơ bên đường_ray xe_lửa Hàng chục_ngàn người Chile biểu_tình chống cải_cách giáo_dục Trung_Quốc chấn_chỉnh loạn thu Sách_giáo_khoa điện_tử ở Pháp Anh giáo_dục giới_tính từ 5 tuổi Tư_nhân_hóa trường công : kinh_nghiệm thế_giới Bài viết Cẩn_trọng khi cho trẻ sử_dụng máy_tính bảng";//StrUtil.join(new ArrayList<String>(Arrays.asList(vietTokenizer.tokenize(test1))));
        double[] testProbabilities1 = inferTopicModel.getTopicDitribution(processedText); 
        System.out.println("Inference time 1: " + (System.currentTimeMillis() - begin));
        for(int i=0; i< testProbabilities1.length; i++){
            System.out.println(i + "\t" + testProbabilities1[i]);
        }
        
        List<Map.Entry<Integer, Double>> resultTopic = new ArrayList<Map.Entry<Integer, Double>>();
        for (int i = 0; i < testProbabilities1.length; i++) {
            Map.Entry<Integer, Double> topic = new AbstractMap.SimpleEntry<Integer, Double>(i, testProbabilities1[i]);
            resultTopic.add(topic);
        }

        //sort
        Collections.sort(resultTopic, m_revertEntryIntDoubleComparator);

        //top 3
        System.out.print("Result: ");
        for (int i = 0; i < 3; i++) {
            Map.Entry<Integer, Double> entry = resultTopic.get(i);
            Integer key = entry.getKey();
            double value = Math.floor(entry.getValue() * 100000) / 100000;
            System.out.print(key + ":" + value + ",");
        }
    }
    private static class RevertEntryIntDoubleComparator implements Comparator<Map.Entry<Integer,Double>> {
		public int compare(Map.Entry<Integer, Double> first, Map.Entry<Integer, Double> last) {
			double value = first.getValue() - last.getValue();
			if(value > 0){
				return -1;
			}else if(value ==0) {
				return 0;
			}else{
				return 1;
			}
		}
	}
}
