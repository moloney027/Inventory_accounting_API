INSERT INTO public.product(
	id, archive, article, last_purchase_price, last_sale_price, name)
	VALUES (101, false, '001', null, null, 'test product 1');

INSERT INTO public.product(
	id, archive, article, last_purchase_price, last_sale_price, name)
	VALUES (102, false, '002', null, null, 'test product 2');

INSERT INTO public.product(
	id, archive, article, last_purchase_price, last_sale_price, name)
	VALUES (103, true, '003', null, null, 'test product 3');

INSERT INTO public.product(
    id, archive, article, last_purchase_price, last_sale_price, name)
    VALUES (104, false, '004', null, null, 'test product 4');


INSERT INTO public.storage(
    id, archive, name)
    VALUES (201, false, 'test storage 1');

INSERT INTO public.storage(
    id, archive, name)
    VALUES (202, false, 'test storage 2');

INSERT INTO public.storage(
    id, archive, name)
    VALUES (203, false, 'test storage 3');

INSERT INTO public.storage(
    id, archive, name)
    VALUES (204, true, 'test storage 4');


INSERT INTO public.document_receipt(
	id, count, number, purchase_price, product_id, storage_id)
	VALUES (301, 25, '00001R', 875.99, 101, 201);

INSERT INTO public.document_receipt(
    id, count, number, purchase_price, product_id, storage_id)
    VALUES (302, 400, '00002R', 100000.0, 102, 202);

INSERT INTO public.document_receipt(
    id, count, number, purchase_price, product_id, storage_id)
    VALUES (303, 965, '00003R', 115.50, 101, 203);

INSERT INTO public.document_receipt(
    id, count, number, purchase_price, product_id, storage_id)
    VALUES (304, 2, '00004R', 9999.99, 104, 202);


INSERT INTO public.document_sale(
    id, count, number, sale_price, product_id, storage_id)
    VALUES (401, 5, '00001S', 1000.0, 101, 201);

INSERT INTO public.document_sale(
    id, count, number, sale_price, product_id, storage_id)
    VALUES (402, 28, '00002S', 150000.0, 102, 202);

INSERT INTO public.document_sale(
    id, count, number, sale_price, product_id, storage_id)
    VALUES (403, 900, '00003S', 200.99, 101, 203);

INSERT INTO public.document_sale(
    id, count, number, sale_price, product_id, storage_id)
    VALUES (404, 1, '00004S', 10000.84, 104, 202);


INSERT INTO public.document_moving(
    id, count, number, from_storage_id, product_id, to_storage_id)
    VALUES (501, 1, '00001M', 201, 101, 202);

INSERT INTO public.document_moving(
    id, count, number, from_storage_id, product_id, to_storage_id)
    VALUES (502, 215, '00002M', 202, 102, 201);

INSERT INTO public.document_moving(
    id, count, number, from_storage_id, product_id, to_storage_id)
    VALUES (503, 30, '00003M', 203, 101, 202);

INSERT INTO public.document_moving(
    id, count, number, from_storage_id, product_id, to_storage_id)
    VALUES (504, 68, '00004M', 202, 104, 203);

INSERT INTO public."user"(
	id, login, password, first_name, last_name, role, password_secret)
	VALUES (0,	'admin', 'hkW6fQ==', 'admin', 'admin', 'ADMIN', '9L5NqBq3uQa6CXWJ+/unXmzOVsmPL7o9HiPox9VGKL0NG94=');
